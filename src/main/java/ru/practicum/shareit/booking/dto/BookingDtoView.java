package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class BookingDtoView {

    @JsonProperty("id")
    private Long id;
    @JsonProperty("start")
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @JsonProperty("end")
    @NotNull
    private LocalDateTime end;
    @JsonProperty("booker")
    @NotNull
    private User booker;
    @JsonProperty("item")
    @NotNull
    private Item item;
    @JsonProperty("status")
    @NotNull
    private String status;
}
