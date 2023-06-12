package ru.practicum.shareit.request.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.dao.ItemRequestStorage;
import ru.practicum.shareit.request.dao.ItemRequestStorageImpl;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collection;

@Service
@Slf4j
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestStorage requestStorage;
    private final UserService userService;
    private final UserMapper userMapper;

    @Autowired
    public ItemRequestServiceImpl(ItemRequestStorageImpl requestStorage, UserServiceImpl userService,
                                  UserMapper userMapper) {
        this.requestStorage = requestStorage;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Override
    public ItemRequest addNewRequest(long userId, ItemRequestDto itemRequestDto) {
        User user = userMapper.toUser(userService.findUserById(userId));

        return requestStorage.addNewRequest(user, itemRequestDto);
    }

    @Override
    public void removeRequest(long id) {

        requestStorage.removeRequest(id);
    }

    @Override
    public void deleteAllRequests() {

        requestStorage.deleteAllRequests();
    }

    @Override
    public ItemRequest updateRequest(long requestId, long requestorId, ItemRequestDto itemRequestDto) {
        return requestStorage.updateRequest(requestId, requestorId, itemRequestDto);
    }

    @Override
    public Collection<ItemRequestDto> getAllRequests() {
        return requestStorage.getAllRequests();
    }

    @Override
    public ItemRequestDto findRequestById(long id) {
        return requestStorage.findRequestById(id);
    }
}
