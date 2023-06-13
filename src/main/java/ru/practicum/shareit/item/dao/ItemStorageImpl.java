package ru.practicum.shareit.item.dao;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
@Slf4j
public class ItemStorageImpl implements ItemStorage {
    @Autowired
    private ItemMapper itemMapper;
    private final Map<Long, Item> items = new HashMap<>();
    private long id;

    public Map<Long, Item> getItems() {
        return items;
    }

    @Override
    public Item addNewItem(User user, ItemDto itemDto) {
        Item item = itemMapper.toItem(checkValidationItem(itemDto));
        long itemId = ++id;
        Item newItem = item.toBuilder()
                .id(itemId)
                .owner(user).build();
        items.put(itemId, newItem);
        log.info("В приложение добавлена вещь с id {}", itemId);
        return newItem;
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        if (items.containsKey(itemId)) {
            if (items.get(itemId).getOwner().getId() == userId) {
                Item item = items.get(itemId);
                Item updateItem = item.toBuilder()
                        .name(itemDto.getName() != null ? itemDto.getName() : items.get(itemId).getName())
                        .description(itemDto.getDescription() != null ? itemDto.getDescription() :
                                items.get(itemId).getDescription())
                        .available(itemDto.getAvailable() != null ? itemDto.getAvailable() :
                                items.get(itemId).isAvailable()).build();
                items.put(itemId, updateItem);
                log.info("Отредактирована вещь с id {}", itemId);
                return updateItem;
            } else {
                log.error("Id пользователя {} не совпадает с id владельца вещи {}.",
                        userId, items.get(itemId).getOwner().getId());
                throw new UserNotFoundException(String.format("Редактировать вещь c id %d может только ее владелец "
                        + "с id %d", itemId, items.get(itemId).getOwner().getId()));
            }
        } else {
            log.error("Передан некорректный id вещи: {}", itemId);
            throw new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована в приложении.", itemId));
        }
    }

    @Override
    public ItemDto findItemById(long itemId) {
        if (items.containsKey(itemId)) {
            log.info("Запрошена вещь с id {}. Данные получены", itemId);
            return itemMapper.toItemDto(items.get(itemId));
        } else {
            log.error("Передан некорректный id вещи: {}", itemId);
            throw new ItemNotFoundException(String.format("Вещь с id %d не зарегистрирована в приложении.", itemId));
        }
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long userId) {
        List<ItemDto> itemsByOwner =  items.values().stream()
                .filter(i -> i.getOwner().getId() == userId)
                .map(i -> itemMapper.toItemDto(i))
                .collect(Collectors.toList());
        if (itemsByOwner.isEmpty()) {
            log.error("Пользователь с id {} не размещал вещей в приложении.",
                    userId);
            throw new UserNotFoundException(String.format("Владелец вещей с id %d не найден", userId));
        }
        log.info("Пользователь с id {} запросил список своих вещей. Данные получены", userId);
        return itemsByOwner;
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        log.info(String.format("Запрошен список вещей, содержащих в названии или описании - %s.", text));
        if (StringUtils.isBlank(text)) {
            return new ArrayList<>();
        }
        return items.values().stream()
                .filter(i -> i.getName().toLowerCase().contains(text.toLowerCase()) ||
                        i.getDescription().toLowerCase().contains(text.toLowerCase()))
                .filter(i -> i.isAvailable())
                .map(i -> itemMapper.toItemDto(i))
                .collect(Collectors.toList());
    }

    private ItemDto checkValidationItem(ItemDto itemDto) throws ValidationException {

        if (itemDto == null) {
            log.error("Передано пустое тело запроса");
            throw new ValidationException("Тело запроса не может быть пустым.");
        }
        if (StringUtils.isBlank(itemDto.getName())) {
            log.error("Передано некорректное название вещи: {}", itemDto.getName());
            throw new ValidationException("Название вещи не может быть пустым");
        }
        if (StringUtils.isBlank(itemDto.getDescription())) {
            log.error("Передано некорректное описание вещи: {}", itemDto.getDescription());
            throw new ValidationException("Описание вещи не может быть пустым");
        }
        if (itemDto.getAvailable() == null) {
            log.error("Доступность вещи не указана");
            throw new ValidationException("При создании вещи должна быть указана ее доступность. "
                    + "Поле не может быть пустым");
        }
        return itemDto;
    }
}
