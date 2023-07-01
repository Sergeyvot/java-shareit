package ru.practicum.shareit;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exception.DuplicateEmailUserException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.UserNotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestDatabase
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ShareItTests {

	private final UserServiceImpl userService;
	private final ItemServiceImpl itemService;

	@Test
	void contextLoads() {
	}

	@Test
	public void testAddUser() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test@mail.ru").build();
		UserDto user = userService.createUser(userDto);

		assertEquals("test@mail.ru", user.getEmail(),
				"Поля созданного пользователя не совпадают");
		assertEquals("NameTest", user.getName(),
				"Поля созданного пользователя не совпадают");

		UserDto userDto1 = UserDto.builder()
				.name("")
				.email("test1@mail.ru").build();
		UserDto user1 = userService.createUser(userDto1);

		assertEquals("test1@mail.ru", user1.getEmail(),
				"Поля созданного пользователя не совпадают");
		assertEquals("", user1.getName(),
				"Поля созданного пользователя не совпадают");

	}

	@Test
	public void testUpdateUser() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test3@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("")
				.email("test4@mail.ru").build();
		UserDto updateUser = userService.updateUser(user.getId(), userDto1);

		assertEquals("test4@mail.ru", updateUser.getEmail(),
				"Поля обновленного пользователя не совпадают");
		assertEquals("", updateUser.getName(),
				"Поля обновленного пользователя не совпадают");
	}

	@Test
	public void testUpdateUserWithIncorrectId() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test5@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("")
				.email("test6@mail.ru").build();

		Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
			userService.updateUser(9999, userDto1);
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Пользователь с id 9999 не зарегистрирован в базе приложения.");
	}

	@Test
	public void testUpdateUserWithDuplicateEmail() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test31@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("")
				.email("test32@mail.ru").build();
		UserDto user1 = userService.createUser(userDto1);
		UserDto userDto2 = UserDto.builder()
				.name("UpdateName")
				.email("test31@mail.ru").build();
		Throwable thrown = assertThrows(DuplicateEmailUserException.class, () -> {
			userService.updateUser(user1.getId(), userDto2);
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Пользователь с электронной почтой " + userDto2.getEmail()
				+ " уже зарегистрирован в приложении");
	}

	@Test
	public void testFindUserById() {
		UserDto userDto = UserDto.builder()
				.name("NameCheckTest")
				.email("test10@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto checkUserDto = userService.findUserById(user.getId());

		assertEquals("NameCheckTest", checkUserDto.getName(),
				"Поля найденного пользователя не совпадают");
		assertEquals("test10@mail.ru", checkUserDto.getEmail(),
				"Поля найденного пользователя не совпадают");
	}

	@Test
	public void testFindUserByIdWithIncorrectId() {
		Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
			userService.findUserById(9999);
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Пользователь с id 9999 не зарегистрирован в базе приложения.");
	}

	@Test
	public void testFindAllUsers() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test11@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("")
				.email("test12@mail.ru").build();
		UserDto user1 = userService.createUser(userDto1);

		List<UserDto> checkUsers = new ArrayList<>(userService.findAllUsers());

		assertFalse(checkUsers.isEmpty(), "Список пользователей пустой");
		assertEquals(user1.getName(), checkUsers.get(checkUsers.size() - 1).getName(),
				"Поля не совпадают");
	}

	@Test
	public void testAddNewItem() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test13@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		ItemDto itemDto = ItemDto.builder()
				.name("itemNameCheck")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);

		assertEquals("itemNameCheck", item.getName(), "Поля созданной вещи не совпадают.");
		assertEquals(true, item.getAvailable(), "Поля созданной вещи не совпадают.");
	}

	@Test
	public void testUpdateItem() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test14@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		ItemDto itemDto = ItemDto.builder()
				.name("itemNameCheck")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);

		ItemDto updateItemDto = ItemDto.builder()
				.name("updateItemName")
				.description("Почти хорошая вещь")
				.available(false).build();
		ItemDto updateItem = itemService.updateItem(user.getId(), item.getId(), updateItemDto);

		assertEquals("updateItemName", updateItem.getName(),
				"Поля обновленной вещи не совпадают");
		assertEquals(false, updateItem.getAvailable(),
				"Поля обновленной вещи не совпадают");
	}

	@Test
	public void testUpdateItemWithIncorrectItemId() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test15@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		ItemDto itemDto = ItemDto.builder()
				.name("itemNameCheck")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);
		ItemDto updateItemDto = ItemDto.builder()
				.name("updateItemName")
				.description("Почти хорошая вещь")
				.available(false).build();

		Throwable thrown = assertThrows(ItemNotFoundException.class, () -> {
			itemService.updateItem(user.getId(), 9999, updateItemDto);
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Вещь с id 9999 не зарегистрирована в приложении.");
	}

	@Test
	public void testFindItemByIdWithIncorrectId() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test18@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		ItemDto itemDto = ItemDto.builder()
				.name("itemNameCheck")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);

		Throwable thrown = assertThrows(ItemNotFoundException.class, () -> {
			itemService.findItemById(9999, user.getId());
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Вещь с id 9999 не зарегистрирована в приложении.");
	}

	@Test
	public void testGetAllItemsByOwnerIdWithIncorrectId() {
		Throwable thrown = assertThrows(UserNotFoundException.class, () -> {
			itemService.getAllItemsByOwnerId(9999);
		});
		assertNotNull(thrown.getMessage());

		assertEquals(thrown.getMessage(), "Пользователь с id 9999 не зарегистрирован "
				+ "в базе приложения.");
	}

	@Test
	public void testGetItemBySearch() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test22@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("NameTest")
				.email("test23@mail.ru").build();
		UserDto user1 = userService.createUser(userDto1);
		ItemDto itemDto = ItemDto.builder()
				.name("Классная вещь")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);
		ItemDto itemDto1 = ItemDto.builder()
				.name("itemNameCheck1")
				.description("Очень КЛАССНАЯ вещь")
				.available(true).build();
		ItemDto item1 = itemService.addNewItem(user1.getId(), itemDto1);

		List<ItemDto> checkList = itemService.getItemBySearch("клаСС");
		assertFalse(checkList.isEmpty(), "Список пустой");
		assertEquals(2,checkList.size(), "Размер списка не совпадает");
	}

	@Test
	public void testGetItemBySearchWithMissingParameterSearch() {
		UserDto userDto = UserDto.builder()
				.name("NameTest")
				.email("test24@mail.ru").build();
		UserDto user = userService.createUser(userDto);
		UserDto userDto1 = UserDto.builder()
				.name("NameTest")
				.email("test25@mail.ru").build();
		UserDto user1 = userService.createUser(userDto1);
		ItemDto itemDto = ItemDto.builder()
				.name("Классная вещь")
				.description("Очень хорошая вещь")
				.available(true).build();
		ItemDto item = itemService.addNewItem(user.getId(), itemDto);
		ItemDto itemDto1 = ItemDto.builder()
				.name("itemNameCheck1")
				.description("Очень КЛАССНАЯ вещь")
				.available(true).build();
		ItemDto item1 = itemService.addNewItem(user1.getId(), itemDto1);

		List<ItemDto> checkList = itemService.getItemBySearch("Что то");
		assertTrue(checkList.isEmpty(), "Список  не пустой");
	}
}
