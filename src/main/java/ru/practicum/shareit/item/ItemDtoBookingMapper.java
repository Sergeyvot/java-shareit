package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dao.ItemRequestStorageImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;

@Mapper(uses = {ItemRequestMapper.class}, componentModel = "spring")
public interface ItemDtoBookingMapper {

    @Mapping(target = "request", source = "requestId", qualifiedByName = "getRequestFromRequestId")
    Item toItem(ItemDtoBooking itemDtoBooking);

    @Named("getRequestFromRequestId")
    default ItemRequestDto getRequestFromRequestId(Long requestId) {
        ItemRequestStorageImpl requestStorage = new ItemRequestStorageImpl();
        if (requestId != null) {
            return requestStorage.findRequestById(requestId);
        }
        return null;
    }
}
