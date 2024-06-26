package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user, Long userId);

    List<User> getAll();

    User get(long userId);

    void delete(long userId);
}
