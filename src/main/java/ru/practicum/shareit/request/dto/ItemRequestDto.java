package ru.practicum.shareit.request.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
public class ItemRequestDto {

    @JsonProperty("description")
    @NotNull
    @Size(max = 255)
    private String description;
    @JsonProperty("created")
    @NotNull
    private LocalDateTime created;
}
