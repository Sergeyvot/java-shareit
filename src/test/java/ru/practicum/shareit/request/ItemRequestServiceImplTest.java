package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDtoView;
import ru.practicum.shareit.request.service.ItemRequestService;
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
public class ItemRequestServiceImplTest {

    private final ItemRequestService service;
    private final UserService userService;

    @Test
    void testGetAllRequestsOfOtherUsers() {
        UserDto userDto1 = makeUserDto("some@email.com", "UserName");
        UserDto requester = userService.createUser(userDto1);
        UserDto userDto2 = makeUserDto("some@yandex.ru", "Vasilii");
        UserDto user = userService.createUser(userDto2);

        List<ItemRequestDto> sourceRequests = Stream.of(
                makeItemRequestDto("Нужна отвертка"),
                makeItemRequestDto("Нужен чайник"),
                makeItemRequestDto("Нужна дрель")
        ).collect(Collectors.toList());

        for (ItemRequestDto requestDto : sourceRequests) {
            service.createNewRequest(requester.getId(), requestDto);
        }

        List<ItemRequestDtoView> targetRequests = service.getAllRequestsOfOtherUsers(user.getId(), 0, 5);

        assertThat(targetRequests, hasSize(sourceRequests.size()));
        for (ItemRequestDto sourceRequest : sourceRequests) {
            assertThat(targetRequests, hasItem(allOf(
                    hasProperty("id", notNullValue()),
                    hasProperty("description", equalTo(sourceRequest.getDescription())),
                    hasProperty("created", notNullValue()),
                    hasProperty("items", equalTo(new ArrayList<>()))
            )));
        }
    }

    private UserDto makeUserDto(String email, String name) {

        return UserDto.builder()
                .name(name)
                .email(email).build();
    }

    private ItemRequestDto makeItemRequestDto(String description) {

        return ItemRequestDto.builder()
                .description(description).build();
    }
}
