package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingDtoItem;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ItemDtoBooking {
    @JsonProperty("id")
    @PositiveOrZero
    private long id;
    @JsonProperty("name")
    @NotBlank
    private String name;
    @JsonProperty("description")
    @Size(max = 1000)
    private String description;
    @JsonProperty("lastBooking")
    private BookingDtoItem lastBooking;
    @JsonProperty("nextBooking")
    private BookingDtoItem nextBooking;
    @JsonProperty("available")
    private Boolean available;
    @JsonProperty("requestId")
    private Long requestId;
    @JsonProperty("comments")
    private List<CommentDto> comments;
}
