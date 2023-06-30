package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingDtoItemMapper;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.CommentMapperUtil;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.CommentRepository;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository repository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;
    private final BookingDtoItemMapper bookingDtoItemMapper;

    @Override
    public ItemDto addNewItem(long userId, ItemDto itemDto) {
        checkValidationItem(itemDto);
        User user = userMapper.toUser(userService.findUserById(userId));
        Item item = itemMapper.toItem(itemDto);
        Item newItem = item.toBuilder()
                .owner(user).build();
        log.info("В приложение добавлена вещь {}", itemDto.getName());
        return itemMapper.toItemDto(repository.save(newItem));
    }

    @Override
    public ItemDto updateItem(long userId, long itemId, ItemDto itemDto) {
        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована "
                        + "в приложении.", itemId)));
        User user = userMapper.toUser(userService.findUserById(userId));
        if (item.getOwner().getId() == userId) {
            Item updateItem = item.toBuilder()
                    .name(itemDto.getName() != null ? itemDto.getName() : item.getName())
                    .description(itemDto.getDescription() != null ? itemDto.getDescription() :
                            item.getDescription())
                    .available(itemDto.getAvailable() != null ? itemDto.getAvailable() :
                            item.isAvailable()).build();
            log.info("Отредактирована вещь с id {}", itemId);
            return itemMapper.toItemDto(repository.save(updateItem));
        } else {
            log.error("Id пользователя {} не совпадает с id владельца вещи {}.",
                    userId, item.getOwner().getId());
            throw new UserNotFoundException(String.format("Редактировать вещь c id %d может только ее владелец "
                    + "с id %d", itemId, item.getOwner().getId()));
        }
    }

    @Override
    public CommentDto addNewComment(long userId, long itemId, CommentDto commentDto) {

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована "
                        + "в приложении.", itemId)));
        User user = userMapper.toUser(userService.findUserById(userId));
        if (StringUtils.isBlank(commentDto.getText())) {
            log.error("Отзыв не может быть пустым сообщением");
            throw new ValidationException("Отзыв не может быть пустым сообщением");
        }
        if (bookingRepository.findAllByItemId(itemId).stream()
                .anyMatch(b -> b.getBooker().getId() == userId && b.getEnd().isBefore(Instant.now()))) {
            Comment comment = CommentMapperUtil.toComment(user, item, commentDto);
            Comment newComment = comment.toBuilder()
                    .created(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).build();
            log.info("Пользователь с id {} оставил отзыв о вещи с id {}", userId, itemId);
            return CommentMapperUtil.toCommentDto(commentRepository.save(newComment));
        } else {
            log.error("Оставлять отзывы может только пользователь, уже бравший данную вещь в аренду. "
                    + "id пользователя {}, сделавшего запрос, не совпадает.", userId);
            throw new ValidationException(String.format("Пользователь с id %d не брал данную вещь в аренду.", userId));
        }
    }

    @Override
    public ItemDtoBooking findItemById(long itemId, long userId) {

        Item item = repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована "
                        + "в приложении.", itemId)));
        log.info("Запрошена вещь с id {}. Данные получены", itemId);
        return ItemDtoBooking.builder()
                .id(item.getId())
                .name(item.getName())
                .available(item.isAvailable())
                .description(item.getDescription())
                .requestId(item.getRequest() != null ? item.getRequest().getId() : null)
                .lastBooking(bookingRepository.findByItemIdOrderByEndDesc(item.getId(),
                                LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).stream()
                        .filter(b -> b.getItem().getOwner().getId() == userId)
                        .map(bookingDtoItemMapper::toBookingDtoItem)
                        .findFirst().orElse(null))
                .nextBooking(bookingRepository.findByItemIdOrderByStartAsc(item.getId(),
                                LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).stream()
                        .filter(b -> b.getStatus().equals(Status.APPROVED) || b.getStatus().equals(Status.WAITING))
                        .filter(b -> b.getItem().getOwner().getId() == userId)
                        .map(bookingDtoItemMapper::toBookingDtoItem)
                        .findFirst().orElse(null))
                .comments(commentRepository.findAllByItemId(item.getId()).stream()
                        .map(CommentMapperUtil::toCommentDto)
                        .collect(Collectors.toList())).build();
    }

    @Override
    public Item findEntityById(long itemId) {

        return repository.findById(itemId)
                .orElseThrow(() -> new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована "
                        + "в приложении.", itemId)));
    }

    @Override
    public List<ItemDtoBooking> getAllItemsByOwnerId(long userId) {
        User user = userMapper.toUser(userService.findUserById(userId));
        return repository.findAllByOwnerId(userId).stream()
                .map(i -> ItemDtoBooking.builder()
                        .id(i.getId())
                        .name(i.getName())
                        .available(i.isAvailable())
                        .description(i.getDescription())
                        .requestId(i.getRequest() != null ? i.getRequest().getId() : null)
                        .lastBooking(bookingRepository.findByItemIdOrderByEndDesc(i.getId(),
                                        LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).stream()
                                .map(bookingDtoItemMapper::toBookingDtoItem)
                                .findFirst().orElse(null))
                        .nextBooking(bookingRepository.findByItemIdOrderByStartAsc(i.getId(),
                                        LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).stream()
                                .filter(b -> b.getStatus().equals(Status.APPROVED) || b.getStatus().equals(Status.WAITING))
                                .map(bookingDtoItemMapper::toBookingDtoItem)
                                .findFirst().orElse(null))
                        .comments(commentRepository.findAllByItemId(i.getId()).stream()
                                .map(CommentMapperUtil::toCommentDto)
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        log.info(String.format("Запрошен список вещей, содержащих в названии или описании - %s.", text));
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return repository.search(text).stream()
                .filter(Item::isAvailable)
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    private void checkValidationItem(ItemDto itemDto) throws ValidationException {

        if (itemDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(itemDto.getName())) {
            log.error("Передано некорректное название вещи: {}", itemDto.getName());
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (StringUtils.isBlank(itemDto.getDescription())) {
            log.error("Передано некорректное описание вещи: {}", itemDto.getDescription());
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Доступность вещи не указана");
            throw new ValidationException("При создании вещи должна быть указана ее доступность. "
                    + "Поле не может быть пустым");
        }
    }
}
