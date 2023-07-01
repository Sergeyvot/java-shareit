package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.Constant;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoView createNewBooking(@RequestHeader(Constant.CONSTANT_HEADER) Long userId,
                                           @RequestBody BookingDto bookingDto) {
        BookingDtoView result = bookingService.addNewBooking(userId, bookingDto);
        if (result != null) {
            log.info("Запрос выполнен. В приложение добавлено бронирование вещи с id {}", result.getItem().getId());
        } else {
            log.info("Бронирование вещи с id {} не удалось", bookingDto.getItemId());
        }
        return result;
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoView updateApproved(@RequestHeader(Constant.CONSTANT_HEADER) Long userId,
                              @PathVariable long bookingId,
                              @RequestParam(name = "approved") @NotBlank Boolean approved) {
        BookingDtoView result = bookingService.updateApproved(userId, bookingId, approved);
        if (result != null) {
            log.info("Изменена доступность бронирование с id {}", bookingId);
        } else {
            log.info("Доступность бронирования с id {} изменить не удалось", bookingId);
        }
            return result;
    }

    @GetMapping("/{bookingId}")
    public BookingDtoView getBookingById(@RequestHeader(Constant.CONSTANT_HEADER) Long userId,
                                        @PathVariable long bookingId) {
        BookingDtoView result = bookingService.getBookingById(bookingId, userId);
        if (result != null) {
            log.info("Запрошено бронирование с id {}. Данные получены", bookingId);
        } else {
            log.info("Запрос бронирования с id {} не выполнен. Необходимо определить ошибку", bookingId);
        }
        return result;
    }

    @GetMapping
    public List<BookingDtoView> getAllBookingsByBookerId(@RequestHeader(Constant.CONSTANT_HEADER) Long userId,
                                     @RequestParam(defaultValue = "ALL", required = false,
                                             name = "state") String state) {
        List<BookingDtoView> resultList = bookingService.getAllBookingsByBookerId(userId, state);
        if (resultList != null) {
            log.info("Пользователем с id {} запрошен список своих бронирований. Данные получены", userId);
        } else {
            log.info("Запрос списка своих бронирований пользователем с id {} не выполнен. Необходимо определить ошибку",
                    userId);
        }
        return resultList;
    }

    @GetMapping("/owner")
    public List<BookingDtoView> getAllBookingsByOwnerId(@RequestHeader(Constant.CONSTANT_HEADER) Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false,
                                                             name = "state") String state) {
        List<BookingDtoView> resultList = bookingService.getAllBookingsByOwnerId(userId, state);
        if (resultList != null) {
            log.info("Пользователем с id {} запрошен список бронирований своих вещей. Данные получены", userId);
        } else {
            log.info("Запрос списка бронирований своих вещей пользователем с id {} не выполнен. " +
                            "Необходимо определить ошибку", userId);
        }
        return resultList;
    }
}
