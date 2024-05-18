package ru.practicum.shareit.user;

import java.util.List;

public interface UserStorage {

    User create(User user);

    User update(User user, Long userId);

    List<User> getAll();

    User get(long userId);

    void remove(long userId);
}
