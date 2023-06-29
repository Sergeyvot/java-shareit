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

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class BookingDtoItem {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("start")
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @JsonProperty("end")
    @NotNull
    private LocalDateTime end;
    @JsonProperty("bookerId")
    @NotNull
    @PositiveOrZero
    private long bookerId;
    @JsonProperty("itemId")
    @NotNull
    @PositiveOrZero
    private long itemId;
    @JsonProperty("status")
    @NotNull
    private String status;
}
