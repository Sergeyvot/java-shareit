package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoView;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.Month;
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

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @MockBean
    BookingService bookingService;
    @Autowired
    ObjectMapper mapper;
    @Autowired
    private MockMvc mvc;
    private final String CONSTANT_HEADER = "X-Sharer-User-Id";

    private final BookingDtoView bookingDtoView = new BookingDtoView(1L,
            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30),
            LocalDateTime.of(2023, Month.JULY, 11, 12, 30, 30),
            new User(1L, "NameUser", "test@mail.ru"),
            new Item(1L, "Отвертка", "Крестовая отвертка", true,
                    new User(2L, "Owner", "owner@mail.ru"), null), "WAITING");

    private final List<BookingDtoView> list = Stream.of(new BookingDtoView(1L,
            LocalDateTime.of(2023, Month.JULY, 10, 12, 30, 30),
            LocalDateTime.of(2023, Month.JULY, 11, 12, 30, 30),
            new User(1L, "NameUser", "test@mail.ru"),
            new Item(1L, "Отвертка", "Крестовая отвертка", true,
                    new User(1L, "Owner", "owner@mail.ru"), null), "WAITING"),
            new BookingDtoView(2L,
                    LocalDateTime.of(2023, Month.JULY, 20, 12, 30, 30),
                    LocalDateTime.of(2023, Month.JULY, 21, 12, 30, 30),
                    new User(1L, "NameUser", "test@mail.ru"),
                    new Item(2L, "Чайник", "Новый чайник", true,
                            new User(2L, "Owner", "owner@mail.ru"), null), "WAITING"))
            .collect(Collectors.toList());

    @Test
    void testCreateNewBooking() throws Exception {
        when(bookingService.addNewBooking(anyLong(), any()))
                .thenReturn(bookingDtoView);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(bookingDtoView))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoView.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingDtoView.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingDtoView.getEnd()))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoView.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoView.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoView.getStatus())));
    }

    @Test
    void testUpdateApproved() throws Exception {
        when(bookingService.updateApproved(anyLong(), anyLong(), any()))
                .thenReturn(bookingDtoView);

        mvc.perform(patch("/bookings/1?approved=true")
                        .content(mapper.writeValueAsString(bookingDtoView))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoView.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingDtoView.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingDtoView.getEnd()))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoView.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoView.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoView.getStatus())));
    }

    @Test
    void testGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoView);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDtoView))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(bookingDtoView.getId()), Long.class))
                .andExpect(jsonPath("$.start", is(String.valueOf(bookingDtoView.getStart()))))
                .andExpect(jsonPath("$.end", is(String.valueOf(bookingDtoView.getEnd()))))
                .andExpect(jsonPath("$.booker.id", is(bookingDtoView.getBooker().getId().intValue())))
                .andExpect(jsonPath("$.item.name", is(bookingDtoView.getItem().getName())))
                .andExpect(jsonPath("$.status", is(bookingDtoView.getStatus())));
    }

    @Test
    void testGetBookingByIdWithoutHeader() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(bookingDtoView);

        mvc.perform(get("/bookings/1")
                        .content(mapper.writeValueAsString(bookingDtoView))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testGetAllBookingsByBookerId() throws Exception {
        when(bookingService.getAllBookingsByBookerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/bookings?state=ALL&from=0&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(String.valueOf(list.get(0).getStart()))))
                .andExpect(jsonPath("$[0].booker.id", is(list.get(0).getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.name", is(list.get(0).getItem().getName())))
                .andExpect(jsonPath("$[1].id", is(list.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].end", is(String.valueOf(list.get(1).getEnd()))))
                .andExpect(jsonPath("$[1].booker.id", is(list.get(1).getBooker().getId().intValue())))
                .andExpect(jsonPath("$[1].status", is(list.get(1).getStatus())));
    }

    @Test
    void testGetAllBookingsByOwnerId() throws Exception {
        when(bookingService.getAllBookingsByOwnerId(anyLong(), anyString(), anyInt(), anyInt()))
                .thenReturn(list);

        mvc.perform(get("/bookings/owner?state=ALL&from=0&size=5")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .header(CONSTANT_HEADER, 1L)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(list.get(0).getId()), Long.class))
                .andExpect(jsonPath("$[0].start", is(String.valueOf(list.get(0).getStart()))))
                .andExpect(jsonPath("$[0].booker.id", is(list.get(0).getBooker().getId().intValue())))
                .andExpect(jsonPath("$[0].item.name", is(list.get(0).getItem().getName())))
                .andExpect(jsonPath("$[1].id", is(list.get(1).getId()), Long.class))
                .andExpect(jsonPath("$[1].end", is(String.valueOf(list.get(1).getEnd()))))
                .andExpect(jsonPath("$[1].booker.id", is(list.get(1).getBooker().getId().intValue())))
                .andExpect(jsonPath("$[1].status", is(list.get(1).getStatus())));
    }
}
