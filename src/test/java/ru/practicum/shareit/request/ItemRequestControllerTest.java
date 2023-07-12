package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.ItemRequestController;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.service.ItemRequestService;

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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestDto itemRequestDto = new ItemRequestDto(1L, "Нужна отвертка",
            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30));

    private final ItemRequestDtoView itemRequestDtoView = new ItemRequestDtoView(1L, "Нужна отвертка",
            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30),
            new ArrayList<>());

    private final List<ItemRequestDtoView> list = Stream.of(new ItemRequestDtoView(1L, "Нужна отвертка",
                            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30),
                            new ArrayList<>()),
                    new ItemRequestDtoView(2L, "Нужен чайник",
                            LocalDateTime.of(2023, Month.JULY, 20, 12, 30, 30),
                            new ArrayList<>())).collect(Collectors.toList());

    @Test
    void testAddNewRequest() throws Exception {
        when(itemRequestService.createNewRequest(anyLong(), any()))
                .thenReturn(itemRequestDto);

        mvc.perform(post("/requests")
                        .content(mapper.writeValueAsString(itemRequestDto))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDto.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDto.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(itemRequestDto.getCreated()))));
    }

    @Test
    void testGetAllRequestsByRequesterId() throws Exception {
        when(itemRequestService.getAllByRequesterId(anyLong()))
                .thenReturn(list);

        mvc.perform(get("/requests")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created", is(String.valueOf(list.get(0).getCreated()))))
                .andExpect(jsonPath("$[0].items", is(list.get(0).getItems())))
                .andExpect(jsonPath("$[1].id", is(list.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(list.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created", is(String.valueOf(list.get(1).getCreated()))))
                .andExpect(jsonPath("$[1].items", is(list.get(1).getItems())));
    }

    @Test
    void testGetRequestById() throws Exception {
        when(itemRequestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoView);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(itemRequestDtoView.getId()), Long.class))
                .andExpect(jsonPath("$.description", is(itemRequestDtoView.getDescription())))
                .andExpect(jsonPath("$.created", is(String.valueOf(itemRequestDtoView.getCreated()))))
                .andExpect(jsonPath("$.items", is(itemRequestDtoView.getItems())));
    }

    @Test
    void testGetRequestByIdWithoutHeader() throws Exception {
        when(itemRequestService.findRequestById(anyLong(), anyLong()))
                .thenReturn(itemRequestDtoView);

        mvc.perform(get("/requests/1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllRequestsOfOtherUsers() throws Exception {
        when(itemRequestService.getAllRequestsOfOtherUsers(anyLong(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/requests/all")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].description", is(list.get(0).getDescription())))
                .andExpect(jsonPath("$[0].created", is(String.valueOf(list.get(0).getCreated()))))
                .andExpect(jsonPath("$[0].items", is(list.get(0).getItems())))
                .andExpect(jsonPath("$[1].id", is(list.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].description", is(list.get(1).getDescription())))
                .andExpect(jsonPath("$[1].created", is(String.valueOf(list.get(1).getCreated()))))
                .andExpect(jsonPath("$[1].items", is(list.get(1).getItems())));
    }
}
