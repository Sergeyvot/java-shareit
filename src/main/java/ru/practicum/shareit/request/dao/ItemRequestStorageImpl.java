package ru.practicum.shareit.request.dao;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.RequestNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.request.ItemRequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemRequestStorageImpl implements ItemRequestStorage {
    @Autowired
    private ItemRequestMapper requestMapper;
    private final Map<Long, ItemRequest> itemRequests = new HashMap<>();
    private long id;

    @Override
    public ItemRequest addNewRequest(User user, ItemRequestDto itemRequestDto) {
        ItemRequest itemRequest = requestMapper.toItemRequest(itemRequestDto);
        long requestId = ++id;
        itemRequest.toBuilder()
                .requestId(requestId)
                .requestor(user).build();
        itemRequests.put(requestId, itemRequest);
        log.info("В приложение добавлен запрос с id {}", requestId);
        return itemRequest;
    }

    @Override
    public void removeRequest(long id) {
        if (itemRequests.containsKey(id)) {
            itemRequests.remove(id);
            log.info("Удален запрос с id {}", id);
        } else {
            log.error("Передан некорректный id запроса: {}", id);
            throw new UserNotFoundException(String.format("Запроса с id %d нет в базе приложения.", id));
        }
    }

    @Override
    public void deleteAllRequests() {
        itemRequests.clear();
        log.info("Удалены все запросы из приложения");
    }

    @Override
    public ItemRequest updateRequest(long requestId, long requestorId, ItemRequestDto itemRequestDto) {
        if (itemRequests.containsKey(requestId)) {
            if (itemRequests.get(requestId).getRequestor().getId() == requestorId) {
                ItemRequest updateRequest = itemRequests.get(requestId);
                updateRequest.toBuilder().description(itemRequestDto.getDescription())
                        .created(itemRequestDto.getCreated()).build();
                itemRequests.put(updateRequest.getRequestId(), updateRequest);
                log.info("Обновлен запрос с id {}", requestId);
                return updateRequest;
            } else {
                log.error("Id пользователя {} не совпадает с id составителя запроса {}.",
                        requestorId, itemRequests.get(requestId).getRequestor().getId());
                throw new ValidationException(String.format("Редактировать запрос c id %d может только его составитель "
                        + "с id %d", requestId, itemRequests.get(requestId).getRequestor().getId()));
            }
        } else {
            log.error("Передан некорректный id запроса: {}", requestId);
            throw new RequestNotFoundException(String.format("Запрос с id %d не зарегистрирован "
                    + "в базе приложения.", requestId));
        }
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests() {
        return itemRequests.values().stream()
                .map(i -> requestMapper.toItemRequestDto(i))
                .collect(Collectors.toList());
    }

    @Override
    public ItemRequestDto findRequestById(long id) {
        if (itemRequests.containsKey(id)) {
            log.info("Запрошен запрос с id {}. Данные получены", id);
            return requestMapper.toItemRequestDto(itemRequests.get(id));
        } else {
            log.error("Передан некорректный id запроса: {}", id);
            throw new RequestNotFoundException(String.format("Запрос с id %d не зарегистрирован "
                    + "в базе приложения.", id));
        }
    }
}
