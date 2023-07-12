package ru.practicum.shareit.booking.dto;

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
    private Long id;
    @NotNull
    @FutureOrPresent
    private LocalDateTime start;
    @NotNull
    private LocalDateTime end;
    @NotNull
    private User booker;
    @NotNull
    private Item item;
    @NotNull
    private String status;

    @Override
    public String toString() {
        return "BookingDtoView{" +
                "id=" + id +
                ", start=" + start +
                ", end=" + end +
                ", booker=" + booker +
                ", item=" + item +
                ", status='" + status + '\'' +
                '}';
    }
}
