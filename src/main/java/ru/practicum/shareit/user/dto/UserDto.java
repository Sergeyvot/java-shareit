package ru.practicum.shareit.user.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.PositiveOrZero;

@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class UserDto {
    @JsonProperty("id")
    @PositiveOrZero
    private long id;
    @JsonProperty("name")
    private String name;
    @JsonProperty("email")
    private String email;
}
