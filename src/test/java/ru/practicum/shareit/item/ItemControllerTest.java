package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @MockBean
    ItemService itemService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private static final String CONSTANT_HEADER = "X-Sharer-User-Id";

    private final ItemDto itemDto = new ItemDto(1L, "Отвертка",
            "Крестовая отвертка", true, null);

    private final ItemDtoBooking itemDtoBooking = new ItemDtoBooking(1L, "Отвертка",
            "Крестовая отвертка", null, null,
            true, null, new ArrayList<>());

    private final CommentDto commentDto = new CommentDto(1L, "Все понравилось", "Commentator",
            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30));

    private final List<ItemDtoBooking> list = Stream.of(new ItemDtoBooking(1L, "Отвертка",
            "Крестовая отвертка", null, null,
            true, null, new ArrayList<>()), new ItemDtoBooking(2L, "Чайник",
            "Новый чайник", null, null,
            true, null, new ArrayList<>())).collect(Collectors.toList());

    private final List<ItemDto> listDto = Stream.of(new ItemDto(1L, "Отвертка",
            "Крестовая отвертка", true, null),
            new ItemDto(2L, "Чайник",
                    "Новый чайник", true, null)).collect(Collectors.toList());

    @Test
    void testAddNewItem() throws Exception {
        when(itemService.addNewItem(anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(post("/items")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void testUpdateItem() throws Exception {
        when(itemService.updateItem(anyLong(), anyLong(), any()))
                .thenReturn(itemDto);

        mvc.perform(patch("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDto.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDto.getName())))
                .andExpect(jsonPath("$.description", is(itemDto.getDescription())))
                .andExpect(jsonPath("$.available", is(itemDto.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDto.getRequestId())));
    }

    @Test
    void testFindItemById() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoBooking);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemDtoBooking.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(itemDtoBooking.getName())))
                .andExpect(jsonPath("$.description", is(itemDtoBooking.getDescription())))
                .andExpect(jsonPath("$.lastBooking", is(itemDtoBooking.getLastBooking())))
                .andExpect(jsonPath("$.nextBooking", is(itemDtoBooking.getNextBooking())))
                .andExpect(jsonPath("$.available", is(itemDtoBooking.getAvailable())))
                .andExpect(jsonPath("$.requestId", is(itemDtoBooking.getRequestId())))
                .andExpect(jsonPath("$.comments", is(itemDtoBooking.getComments())));
    }

    @Test
    void testFindItemByIdWithoutHeader() throws Exception {
        when(itemService.findItemById(anyLong(), anyLong()))
                .thenReturn(itemDtoBooking);

        mvc.perform(get("/items/1")
                        .content(mapper.writeValueAsString(itemDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAddNewComment() throws Exception {
        when(itemService.addNewComment(anyLong(), anyLong(), any()))
                .thenReturn(commentDto);

        mvc.perform(post("/items/1/comment")
                        .content(mapper.writeValueAsString(commentDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(commentDto.getId()), Long.class))
                .andExpect(jsonPath("$.text", is(commentDto.getText())))
                .andExpect(jsonPath("$.authorName", is(commentDto.getAuthorName())))
                .andExpect(jsonPath("$.created", is(String.valueOf(commentDto.getCreated()))));
    }

    @Test
    void testGetAllItemsByOwnerId() throws Exception {
        when(itemService.getAllItemsByOwnerId(anyLong(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/items?from=0&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(list.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].lastBooking", is(list.get(0).getLastBooking())))
                .andExpect(jsonPath("$[1].name", is(list.get(1).getName())))
                .andExpect(jsonPath("$[1].nextBooking", is(list.get(1).getLastBooking())))
                .andExpect(jsonPath("$[1].available", is(list.get(1).getAvailable())))
                .andExpect(jsonPath("$[1].comments", is(list.get(1).getComments())));
    }

    @Test
    void testGetItemBySearch() throws Exception {
        when(itemService.getItemBySearch(anyString(), anyInt(), anyInt()))
                .thenReturn(listDto);

        mvc.perform(get("/items/search?text=оТверТ&from=0&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(listDto.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].name", is(listDto.get(0).getName())))
                .andExpect(jsonPath("$[0].description", is(listDto.get(0).getDescription())))
                .andExpect(jsonPath("$[0].available", is(listDto.get(0).getAvailable())))
                .andExpect(jsonPath("$[1].name", is(listDto.get(1).getName())))
                .andExpect(jsonPath("$[1].description", is(listDto.get(1).getDescription())))
                .andExpect(jsonPath("$[1].available", is(listDto.get(1).getAvailable())))
                .andExpect(jsonPath("$[1].requestId", is(listDto.get(1).getRequestId())));
    }
}
