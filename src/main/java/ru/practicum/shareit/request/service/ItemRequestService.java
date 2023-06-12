package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.Collection;

public interface ItemRequestService {

    ItemRequest addNewRequest(long userId, ItemRequestDto itemRequestDto);

    void removeRequest(long id);

    void deleteAllRequests();

    ItemRequest updateRequest(long requestId, long requestorId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllRequests();

    ItemRequestDto findRequestById(long id);
}
