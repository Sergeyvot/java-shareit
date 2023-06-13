package ru.practicum.shareit.review.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ReviewDto {

    @JsonProperty("content")
    @NotNull
    @Size(max = 255)
    private String content;
    @JsonProperty("positive")
    @NotNull
    private boolean positive;
    @JsonProperty("userId")
    @PositiveOrZero
    private long userId;
    @JsonProperty("itemId")
    @PositiveOrZero
    private long itemId;
}
