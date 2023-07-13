package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.service.ItemRequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {

    private final ItemRequestService itemRequestService;
    private final String CONSTANT_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ItemRequestDto addNewRequest(@RequestHeader(CONSTANT_HEADER) Long userId,
                                            @RequestBody ItemRequestDto itemRequestDto) {
        ItemRequestDto result = itemRequestService.createNewRequest(userId, itemRequestDto);
        if (result != null) {
            log.info("Запрос выполнен. В приложение добавлен запрос с id {}", result.getId());
        } else {
            log.info("Добавление запроса в приложение не удалось. Необходимо определить ошибку");
        }
        return result;
    }

    @GetMapping
    public List<ItemRequestDtoView> getAllRequestsByRequesterId(@RequestHeader(CONSTANT_HEADER) Long userId) {
        List<ItemRequestDtoView> resultList = itemRequestService.getAllByRequesterId(userId);
        if (resultList != null) {
            log.info("Пользователем с id {} запрошен список своих запросов. Данные получены", userId);
        } else {
            log.info("Получение списка своих запросов пользователем с id {} не выполнено. Необходимо определить ошибку",
                    userId);
        }
        return resultList;
    }

    @GetMapping("/{requestId}")
    public ItemRequestDtoView getRequestById(@RequestHeader(CONSTANT_HEADER) Long userId,
                                         @PathVariable long requestId) {
        ItemRequestDtoView result = itemRequestService.findRequestById(requestId, userId);
        if (result != null) {
            log.info("Запрошен просмотр запроса с id {}. Данные получены", requestId);
        } else {
            log.info("Получение запроса с id {} не выполнено. Необходимо определить ошибку", requestId);
        }
        return result;
    }

    @GetMapping("/all")
    public List<ItemRequestDtoView> getAllRequestsOfOtherUsers(@RequestHeader(CONSTANT_HEADER) Long userId,
                                                        @RequestParam(defaultValue = "0", required = false) Integer from,
                                                        @RequestParam(defaultValue = "20", required = false) Integer size) {
        List<ItemRequestDtoView> resultList = itemRequestService.getAllRequestsOfOtherUsers(userId, from, size);
        if (resultList != null) {
            log.info("Пользователем с id {} запрошен постраничный список всех запросов. Данные получены", userId);
        } else {
            log.info("Получение списка всех запросов пользователем с id {} не выполнено. Необходимо определить ошибку",
                    userId);
        }
        return resultList;
    }
}
