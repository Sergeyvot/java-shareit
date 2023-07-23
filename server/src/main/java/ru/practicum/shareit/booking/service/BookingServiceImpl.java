package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.*;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.ParameterStateException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository repository;
    private final ItemService itemService;
    private final UserService userService;
    private final UserMapper userMapper;
    private final BookingDtoViewMapper bookingDtoViewMapper;

    @Override
    public BookingDtoView addNewBooking(Long userId, BookingDto bookingDto) {
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null ||
                bookingDto.getEnd().isBefore(bookingDto.getStart()) || bookingDto.getStart().equals(bookingDto.getEnd()) ||
                bookingDto.getStart().isBefore(LocalDateTime.now())) {
            log.error("Время бронирования задано некорректно.");
            throw new ValidationException("Время бронирования задано некорректно.");
        }
        User user = userMapper.toUser(userService.findUserById(userId));
        Item item = itemService.findEntityById(bookingDto.getItemId());
        if (Objects.equals(item.getOwner().getId(), userId)) {
            log.error("Владелец вещи не должен быть автором бронирования");
            throw new UserNotFoundException("Владелец вещи не должен быть автором бронирования");
        }
        if (!item.isAvailable()) {
            log.error("Вещь с id {} не доступна для бронирования", item.getId());
            throw new ValidationException(String.format("Вещь с id %d не доступна для бронирования", item.getId()));
        }
        Booking newBooking = BookingDtoMapperUtil.toBooking(bookingDto, item).toBuilder()
                .item(item)
                .booker(user)
                .status(Status.WAITING).build();
        log.info("В приложение добавлено бронирование вещи с id {}", item.getId());
        return bookingDtoViewMapper.toBookingDtoView(repository.save(newBooking));
    }

    @Override
    public BookingDtoView updateApproved(Long userId, Long bookingId, Boolean approved) {
        User user = userMapper.toUser(userService.findUserById(userId));
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("Бронирование с id %d не зарегистрировано "
                        + "в базе приложения.", bookingId)));
        if (booking.getStatus().equals(Status.APPROVED) && approved) {
            log.error("Бронирование вещи с id {} уже подтверждено", booking.getItem().getId());
            throw new ValidationException(String.format("Бронирование вещи с id %d уже подтверждено",
                    booking.getItem().getId()));
        }
        if (Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            Booking updateBooking = booking.toBuilder()
                    .status(approved ? Status.APPROVED : Status.REJECTED).build();
            log.info("Отредактировано бронирование с id {}", booking.getId());
            return bookingDtoViewMapper.toBookingDtoView(repository.save(updateBooking));
        } else {
            log.error("Изменить статус бронирования может только владелец вещи. "
                    + "id пользователя {}, сделавшего запрос, не совпадает.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не является владельцем вещи", userId));
        }
    }

    @Override
    public BookingDtoView getBookingById(Long bookingId, Long userId) {
        User user = userMapper.toUser(userService.findUserById(userId));
        Booking booking = repository.findById(bookingId)
                .orElseThrow(() -> new BookingNotFoundException(String.format("<Бронирование с id %d не зарегистрировано "
                        + "в базе приложения.", bookingId)));
        if (Objects.equals(user.getId(), booking.getBooker().getId()) ||
                Objects.equals(user.getId(), booking.getItem().getOwner().getId())) {
            return bookingDtoViewMapper.toBookingDtoView(booking);
        } else {
            log.error("Данные о бронировании может получить только автор бронирования либо владелец вещи. "
                    + "id пользователя {}, сделавшего запрос, не совпадает.", userId);
            throw new UserNotFoundException(String.format("Пользователь с id %d не является автором бронирования "
                    + "либо владельцем вещи", userId));
        }
    }

    @Override
    public List<BookingDtoView> getAllBookingsByBookerId(Long userId, String state, Integer from, Integer size) {
        userService.findUserById(userId);
        checkPaginationParams(from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (State.getEnum(state)) {
            case WAITING:
                return repository.findByBookerIdAndByStatus(userId, "WAITING", pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case REJECTED:
                return repository.findByBookerIdAndByStatus(userId, "REJECTED", pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case ALL:
                return repository.findByBookerId(userId, pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case CURRENT:
                return repository.findByBookerIdCurrent(userId, pageable)
                        .stream()
                        .filter(b -> b.getStart().isBefore(Instant.now()) && b.getEnd().isAfter(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case FUTURE:
                return repository.findByBookerId(userId, pageable)
                        .stream()
                        .filter(b -> b.getStart().isAfter(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case PAST:
                return repository.findByBookerId(userId, pageable)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            default:
                throw new ParameterStateException("Unknown state: " + state);
        }
    }

    @Override
    public List<BookingDtoView> getAllBookingsByOwnerId(Long userId, String state, Integer from, Integer size) {
        userService.findUserById(userId);
        checkPaginationParams(from, size);
        Pageable pageable = PageRequest.of(from > 0 ? from / size : 0, size);

        switch (State.getEnum(state)) {
            case WAITING:
                return repository.findAllByOwner_IdByStatus(userId, "WAITING", pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case REJECTED:
                return repository.findAllByOwner_IdByStatus(userId, "REJECTED", pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case ALL:
                return repository.findAllByOwnerId(userId, pageable)
                        .stream()
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case CURRENT:
                return repository.findAllByOwnerId(userId, pageable)
                        .stream()
                        .filter(b -> b.getStart().isBefore(Instant.now()) && b.getEnd().isAfter(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case FUTURE:
                return repository.findAllByOwnerId(userId, pageable)
                        .stream()
                        .filter(b -> b.getStart().isAfter(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            case PAST:
                return repository.findAllByOwnerId(userId, pageable)
                        .stream()
                        .filter(b -> b.getEnd().isBefore(Instant.now()))
                        .map(bookingDtoViewMapper::toBookingDtoView)
                        .collect(Collectors.toList());
            default:
                throw new ParameterStateException("Unknown state: " + state);
        }
    }

    private void checkPaginationParams(Integer from, Integer size) {
        if (from < 0 || size < 0 || (from.equals(0) && size.equals(0))) {
            log.error("Переданы некорректные параметры постраничного вывода");
            throw new ValidationException("Переданы некорректные параметры постраничного вывода");
        }
    }
}
