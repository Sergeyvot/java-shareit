package ru.practicum.shareit.user.model;

import lombok.Builder;
import lombok.Value;

/**
 * TODO Sprint add-controllers.
 */
@Value
@Builder(toBuilder = true)
public class User {
    long id;
    String name;
    String email;
}
