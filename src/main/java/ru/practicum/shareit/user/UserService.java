package ru.practicum.shareit.user;

import java.util.List;

public interface UserService {

    UserDto create(User user);

    UserDto update(User user,Long userId);

    List<UserDto> getAll();

    UserDto get(long userId);

    void remove(long userId);
}
