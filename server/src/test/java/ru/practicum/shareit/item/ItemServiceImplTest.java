package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceImplTest {

    private final ItemService service;
    private final UserService userService;

    @Test
    void testGetAllItemsByOwnerId() {
        UserDto userDto = makeUserDto("some@email.com", "UserName");
        UserDto newUserDto = userService.createUser(userDto);
        List<ItemDto> sourceItems = Stream.of(
                makeItemDto("Отвертка", "Крестовая отвертка", true),
                makeItemDto("Чайник", "Новый чайник", true),
                makeItemDto("Дрель", "Аккумуляторная дрель", true)
        ).collect(Collectors.toList());

        for (ItemDto item : sourceItems) {
            service.addNewItem(newUserDto.getId(), item);
        }

        List<ItemDtoBooking> targetItems = service.getAllItemsByOwnerId(newUserDto.getId(), 0, 5);

        assertThat(targetItems, hasSize(sourceItems.size()));
        for (ItemDto sourceItem : sourceItems) {
            assertThat(targetItems, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("name", equalTo(sourceItem.getName())),
                    hasProperty("description", equalTo(sourceItem.getDescription())),
                    hasProperty("available", equalTo(sourceItem.getAvailable())),
                    hasProperty("lastBooking", nullValue()),
                    hasProperty("nextBooking", nullValue()),
                    hasProperty("requestId", nullValue()),
                    hasProperty("comments", equalTo(new ArrayList<>()))
            )));
        }
    }

    private UserDto makeUserDto(String email, String name) {

        return UserDto.builder()
                .name(name)
                .email(email).build();
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {

        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available).build();
    }
}
