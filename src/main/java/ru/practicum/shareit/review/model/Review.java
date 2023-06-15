package ru.practicum.shareit.review.model;

import lombok.Builder;
import lombok.Value;

@Value
@Builder(toBuilder = true)
public class Review {

    long reviewId;
    String content;
    boolean positive;
    long userId;
    long itemId;
}
