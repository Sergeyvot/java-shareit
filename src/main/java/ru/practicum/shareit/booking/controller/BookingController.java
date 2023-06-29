package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
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
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingDtoView createNewBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                           @RequestBody BookingDto bookingDto) {
        return bookingService.addNewBooking(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoView updateApproved(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @PathVariable long bookingId,
                              @RequestParam(name = "approved") @NotBlank Boolean approved) {
        return bookingService.updateApproved(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoView getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                        @PathVariable long bookingId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDtoView> getAllBookingsByBookerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @RequestParam(defaultValue = "ALL", required = false,
                                             name = "state") String state) {
        return bookingService.getAllBookingsByBookerId(userId, state);
    }

    @GetMapping("/owner")
    public List<BookingDtoView> getAllBookingsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                     @RequestParam(defaultValue = "ALL", required = false,
                                                             name = "state") String state) {
        return bookingService.getAllBookingsByOwnerId(userId, state);
    }
}
