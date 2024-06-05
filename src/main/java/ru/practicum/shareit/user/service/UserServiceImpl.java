package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserStorage userStorage;

    @Override
    public UserDto create(User user) {
        if (isUserAlreadyExist(user)) {
            log.debug("Пользователь с таким email уже существует");
            throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }
      return UserMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(User user, Long userId) {
        get(userId);

        if (user.getEmail() != null && isEmailAlreadyExistsWhenUpdatesUser(user, userId)) {
                log.debug("Пользователь с таким email уже существует");
                throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }

       return UserMapper.toUserDto(userStorage.update(user,userId));
    }

    @Override
    public List<UserDto> getAll() {
        if (userStorage.getAll().isEmpty()) return new ArrayList<>();
        return userStorage.getAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Override
    public User get(long userId) {
        if (!userStorage.getAll().contains(userStorage.get(userId))) {
            log.debug("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        return userStorage.get(userId);
    }

    @Override
    public void delete(long userId) {
        userStorage.delete(userId);
    }

    private boolean isUserAlreadyExist(User user) {
        return userStorage.getAll().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }

    private boolean isEmailAlreadyExistsWhenUpdatesUser(User user, Long userId) {
        return userStorage.getAll().stream()
                .filter(v -> v.getId() != userId)
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }
}
