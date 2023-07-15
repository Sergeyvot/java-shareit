package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

public interface ItemRequestService {

    ItemRequestDto createNewRequest(long userId, ItemRequestDto itemRequestDto);

    ItemRequest findEntityById(long requestId);

    List<ItemRequestDtoView> getAllByRequesterId(long userId);

    List<ItemRequestDtoView> getAllRequestsOfOtherUsers(long userId, Integer from, Integer size);

    ItemRequestDtoView findRequestById(long requestId, long userId);
}
