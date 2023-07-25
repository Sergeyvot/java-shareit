package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingState;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.exception.ValidationExceptionGateway;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingClientController {
    private final BookingClient bookingClient;
    private static final String CONSTANT_HEADER = "X-Sharer-User-Id";

    @GetMapping
    public ResponseEntity<Object> getBookings(@RequestHeader(CONSTANT_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        checkPaginationParamsGateway(from, size);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookings(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsOwner(@RequestHeader(CONSTANT_HEADER) long userId,
                                              @RequestParam(name = "state", defaultValue = "all") String stateParam,
                                              @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
        BookingState state = BookingState.from(stateParam)
                .orElseThrow(() -> new UnsupportedStatusException("Unknown state: " + stateParam));
        checkPaginationParamsGateway(from, size);
        log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
        return bookingClient.getBookingsOwner(userId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> bookItem(@RequestHeader(CONSTANT_HEADER) long userId,
                                           @RequestBody @Valid BookItemRequestDto requestDto) {
        if (requestDto.getStart() == null || requestDto.getEnd() == null ||
                requestDto.getEnd().isBefore(requestDto.getStart()) || requestDto.getStart().equals(requestDto.getEnd()) ||
                requestDto.getStart().isBefore(LocalDateTime.now())) {
            log.error("Время бронирования задано некорректно.");
            throw new ValidationExceptionGateway("Время бронирования задано некорректно.");
        }
        log.info("Creating booking {}, userId={}", requestDto, userId);
        return bookingClient.bookItem(userId, requestDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateApproved(@RequestHeader(CONSTANT_HEADER) long userId,
                                         @PathVariable long bookingId,
                                         @RequestParam(name = "approved") @NotNull Boolean approved) {
        log.info("Updating booking {}, userId={}", bookingId, userId);
        return bookingClient.updateBook(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBooking(@RequestHeader(CONSTANT_HEADER) long userId,
                                             @PathVariable Long bookingId) {
        log.info("Get booking {}, userId={}", bookingId, userId);
        return bookingClient.getBooking(userId, bookingId);
    }

    private void checkPaginationParamsGateway(Integer from, Integer size) {
        if (from < 0 || size < 0 || (from.equals(0) && size.equals(0))) {
            log.error("Переданы некорректные параметры постраничного вывода");
            throw new ValidationExceptionGateway("Переданы некорректные параметры постраничного вывода");
        }
    }
}
