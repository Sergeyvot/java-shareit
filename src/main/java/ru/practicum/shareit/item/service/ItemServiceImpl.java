package ru.practicum.shareit.item.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dao.ItemStorage;
import ru.practicum.shareit.item.dao.ItemStorageImpl;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

@Service
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public ItemServiceImpl(ItemStorageImpl itemStorage, UserServiceImpl userService, UserMapper userMapper) {
        this.itemStorage = itemStorage;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public Item addNewItem(long userId, ItemDto itemDto) {
        User user = userMapper.toUser(userService.findUserById(userId));

        return itemStorage.addNewItem(user, itemDto);
    }

    @Override
    public Item updateItem(long userId, long itemId, ItemDto itemDto) {
        return itemStorage.updateItem(userId, itemId, itemDto);
    }

    @Override
    public ItemDto findItemById(long itemId) {
        return itemStorage.findItemById(itemId);
    }

    @Override
    public List<ItemDto> getAllItemsByOwnerId(long userId) {
        User user = userMapper.toUser(userService.findUserById(userId));
        return itemStorage.getAllItemsByOwnerId(user.getId());
    }

    @Override
    public List<ItemDto> getItemBySearch(String text) {
        return itemStorage.getItemBySearch(text);
    }
}
