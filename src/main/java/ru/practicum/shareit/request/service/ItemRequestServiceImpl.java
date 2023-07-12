package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dao.ItemRepository;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository repository;
    private final UserService userService;
    private final UserMapper userMapper;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;
    private final ItemRepository itemRepository;

    @Override
    public ItemRequestDto createNewRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userMapper.toUser(userService.findUserById(userId));
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                    + "в базе приложения.", userId));
        }
        if (itemRequestDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(itemRequestDto.getDescription()) ||
                itemRequestDto.getDescription() == null) {
            log.error("Передано некорректное описание вещи: {}", itemRequestDto.getDescription());
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        ItemRequest newItemRequest = itemRequest.toBuilder()
                .requestor(user)
                .created(LocalDateTime.now().toInstant(OffsetDateTime.now().getOffset())).build();

        log.info("Пользователь с id {} оставил запрос в приложении", user.getId());
        return itemRequestMapper.toItemRequestDto(repository.save(newItemRequest));
    }

    @Override
    public ItemRequest findEntityById(long requestId) {

        return repository.findById(requestId)
                .orElseThrow(() -> new RequestNotFoundException(String.format("Запрос с id %d не зарегистрирован "
                        + "в приложении.", requestId)));
    }

    @Override
    public List<ItemRequestDtoView> getAllByRequesterId(long userId) {
        User user = userMapper.toUser(userService.findUserById(userId));
        if (user == null) {
            throw new UserNotFoundException(String.format("Пользователь с id %d не зарегистрирован "
                    + "в базе приложения.", userId));
        }

        return repository.findAllByRequestorIdOrderByCreatedDesc(userId)
                .stream()
                .map(i -> ItemRequestDtoView.builder()
                        .id(i.getId())
                        .description(i.getDescription())
                        .created(LocalDateTime.ofInstant(i.getCreated(), ZoneId.systemDefault()))
                        .items(itemRepository.findAll().stream()
                                .filter(item -> Objects.equals(item.getRequest() != null ? item.getRequest().getId() : null, i.getId()))
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemRequestDtoView> getAllRequestsOfOtherUsers(long userId, Integer from, Integer size) {
        checkPaginationParams(from, size);
        userService.findUserById(userId);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        return repository.findAllOrderByCreatedDesc(userId, pageable)
                .stream()
                .filter(i -> i.getRequestor().getId() != userId)
                .map(i -> ItemRequestDtoView.builder()
                        .id(i.getId())
                        .description(i.getDescription())
                        .created(LocalDateTime.ofInstant(i.getCreated(), ZoneId.systemDefault()))
                        .items(itemRepository.findAll().stream()
                                .filter(item -> Objects.equals(item.getRequest() != null ? item.getRequest().getId() : null, i.getId()))
                                .map(itemMapper::toItemDto)
                                .collect(Collectors.toList())).build())
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDtoView findRequestById(long requestId, long userId) {
        userService.findUserById(userId);
        ItemRequest itemRequest = this.findEntityById(requestId);

        return ItemRequestDtoView.builder()
                .id(itemRequest.getId())
                .description(itemRequest.getDescription())
                .created(LocalDateTime.ofInstant(itemRequest.getCreated(), ZoneId.systemDefault()))
                .items(itemRepository.findAll().stream()
                        .filter(item -> Objects.equals(item.getRequest() != null ? item.getRequest().getId() : null, requestId))
                        .map(itemMapper::toItemDto)
                        .collect(Collectors.toList())).build();
    }

    private void checkPaginationParams(Integer from, Integer size) {
        if (from < 0 || size < 0 || (from.equals(0) && size.equals(0))) {
            log.error("Переданы некорректные параметры постраничного вывода");
            throw new ValidationException("Переданы некорректные параметры постраничного вывода");
        }
    }
}
