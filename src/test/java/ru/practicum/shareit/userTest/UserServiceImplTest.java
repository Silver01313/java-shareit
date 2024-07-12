package ru.practicum.shareit.userTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceImplTest {

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

    @Test
    void updateShouldChangeExistingUser() {
        User newUser = new User();
        newUser.setName("TestName2");
        newUser.setEmail("test@example2.com");

        when(userRepository.findById(1L)).thenReturn(user);
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        UserDto result = userService.update(newUser, 1L);

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getEmail(), result.getEmail());
    }

    @Test
    void updateShouldThrowNotFoundException() {
        when(userRepository.findById(999L)).thenThrow(new NotFoundException("Пользователь не найден"));

        assertThrows(NotFoundException.class, () -> userService.update(new User(), 999L));
    }

    @Test
    void getAllShouldReturnListOfUserDto() {
        when(userRepository.findAll()).thenReturn(List.of(user));

        List<UserDto> result = userService.getAll();

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(userDto.getName(), result.get(0).getName());
        assertEquals(userDto.getEmail(), result.get(0).getEmail());
    }

    @Test
    void getShouldThrowWhenUserNotExists() {
        when(userRepository.findById(999L)).thenThrow(new NotFoundException("Такого пользователя не существует"));

        assertThrows(NotFoundException.class, () -> userService.get(999L));
    }

    @Test
    void deleteShouldCallDeleteById() {
        doNothing().when(userRepository).deleteById(anyLong());

        userService.delete(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }
}
