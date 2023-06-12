package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Value
@Builder(toBuilder = true)
public class ItemRequest {
    long requestId;
    String description;
    User requestor;
    LocalDateTime created;
}
