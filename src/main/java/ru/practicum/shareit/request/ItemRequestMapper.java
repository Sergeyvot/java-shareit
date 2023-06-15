package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper {

    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}
