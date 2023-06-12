package ru.practicum.shareit.item.model;

import lombok.Builder;
import lombok.Value;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

/**
 * TODO Sprint add-controllers.
 */
@Value
@Builder(toBuilder = true)
public class Item {
    long id;
    String name;
    String description;
    boolean available;
    User owner;
    ItemRequest request;
}
