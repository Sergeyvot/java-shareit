package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("id")
    private Long id;
    @JsonProperty("text")
    @NotBlank
    @Size(max = 1000)
    private String text;
    @JsonProperty("authorName")
    private String authorName;
    @JsonProperty("created")
    @FutureOrPresent
    private LocalDateTime created;
}
