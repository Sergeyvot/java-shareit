package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestStorageImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(uses = {ItemRequestMapper.class}, componentModel = "spring")
public interface ItemMapper {
    @Mapping(target = "requestId", source = "request", qualifiedByName = "getRequestIdFromRequest")
    ItemDto toItemDto(Item item);

    @Mapping(target = "request", source = "requestId", qualifiedByName = "getRequestFromRequestId")
    Item toItem(ItemDto itemDto);

    @Named("getRequestIdFromRequest")
    default Long getRequestIdFromRequest(ItemRequest request) {

        return request != null ? request.getRequestId() : null;
    }

    @Named("getRequestFromRequestId")
    default ItemRequestDto getRequestFromRequestId(Long requestId) {
        ItemRequestStorageImpl requestStorage = new ItemRequestStorageImpl();
        if (requestId != null) {
            return requestStorage.findRequestById(requestId);
        }
        return null;
    }
}
