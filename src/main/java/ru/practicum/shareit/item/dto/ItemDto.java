package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

/**
 * TODO Sprint add-controllers.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
public class ItemDto {
    @JsonProperty("id")
    @PositiveOrZero
    private long id;
    @JsonProperty("name")
    @NotBlank
    private String name;
    @JsonProperty("description")
    @Size(max = 1000)
    private String description;
    @JsonProperty("available")
    private Boolean available;
    @JsonProperty("requestId")
    private Long requestId;
}
