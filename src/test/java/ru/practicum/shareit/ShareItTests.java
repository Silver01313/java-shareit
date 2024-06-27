package ru.practicum.shareit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ShareItTests {

	@Mock
	private UserRepository userRepository;

	@InjectMocks
	private UserServiceImpl userService;

	private User user;
	private UserDto userDto;
	@BeforeEach
	void createUser() {
		user = new User();
		user.setId(1L);
		user.setName("TestName");
		user.setEmail("test@example.com");

		userDto = new UserDto(user.getId(), user.getName(), user.getEmail());
	}

	@Test
	void createShouldReturnUserDto() {
		when(userRepository.save(user)).thenReturn(user);

		UserDto result = userService.create(user);

		assertNotNull(result);
		assertEquals(userDto.getName(), result.getName());
		assertEquals(userDto.getEmail(), result.getEmail());
	}

}
