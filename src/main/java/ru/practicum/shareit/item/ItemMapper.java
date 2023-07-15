package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(uses = {ItemRequestMapper.class}, componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "requestId", source = "request", qualifiedByName = "getRequestIdFromRequest")
    ItemDto toItemDto(Item item);

    @Named("getRequestIdFromRequest")
    default Long getRequestIdFromRequest(ItemRequest request) {

        return request != null ? request.getId() : null;
    }
}
