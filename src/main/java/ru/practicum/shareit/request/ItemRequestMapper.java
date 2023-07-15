package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    @Mapping(target = "created", source = "created", qualifiedByName = "getInstantFromLocalDateTime")
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    @Mapping(target = "created", source = "created", qualifiedByName = "getLocalDateTimeFromInstant")
    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);

    @Named("getLocalDateTimeFromInstant")
    default LocalDateTime getLocalDateTimeFromInstant(Instant created) {
        return LocalDateTime.ofInstant(created, ZoneId.systemDefault());
    }

    @Named("getInstantFromLocalDateTime")
    default Instant getInstantFromLocalDateTime(LocalDateTime created) {
        return created != null ? created.toInstant(OffsetDateTime.now().getOffset()) : null;
    }
}
