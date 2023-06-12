package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class BookingDto {

    @JsonProperty("start")
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @JsonProperty("end")
    @NotNull
    private LocalDateTime end;
    @JsonProperty("itemId")
    @NotNull
    @PositiveOrZero
    private long itemId;
    @JsonProperty("bookerId")
    @NotNull
    @PositiveOrZero
    private long bookerId;
    @JsonProperty("status")
    @NotNull
    private String status;
}
