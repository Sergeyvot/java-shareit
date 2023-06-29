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
@Builder(toBuilder = true)
@Getter
public class BookingDto {
    @JsonProperty("id")
    private Long id;
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
    @JsonProperty("status")
    @NotNull
    private String status;
}
