package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.*;
import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class CommentDto {
    private Long id;
    @NotBlank
    @Size(max = 1000)
    private String text;
    private String authorName;
    @FutureOrPresent
    private LocalDateTime created;
}
