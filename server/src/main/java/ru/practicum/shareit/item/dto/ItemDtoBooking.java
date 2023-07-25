package ru.practicum.shareit.item.dto;

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
    @PositiveOrZero
    private long id;
    @NotBlank
    private String name;
    @Size(max = 1000)
    private String description;
    private BookingDtoItem lastBooking;
    private BookingDtoItem nextBooking;
    private Boolean available;
    private Long requestId;
    private List<CommentDto> comments;
}
