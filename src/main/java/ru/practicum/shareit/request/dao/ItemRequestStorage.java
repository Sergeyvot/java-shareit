package ru.practicum.shareit.request.dao;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;

public interface ItemRequestStorage {

    ItemRequest addNewRequest(User user, ItemRequestDto itemRequestDto);

    void removeRequest(long id);

    void deleteAllRequests();

    ItemRequest updateRequest(long requestId, long requestorId, ItemRequestDto itemRequestDto);

    Collection<ItemRequestDto> getAllRequests();

    ItemRequestDto findRequestById(long id);
}
