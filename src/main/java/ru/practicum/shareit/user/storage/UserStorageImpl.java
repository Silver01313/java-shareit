package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
@Slf4j
public class UserStorageImpl implements UserStorage {

    private final HashMap<Long, User> users = new HashMap<>();

    private long id = 0;

    @Override
    public User create(User user) {
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public User update(User user, Long userId) {

        User newUser = users.get(userId);

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }

        if (user.getEmail() != null) {
            newUser.setEmail(user.getEmail());
        }

        users.put(userId, newUser);
        log.info("Пользователь обновлен");
        return newUser;
    }

    @Override
    public List<User> getAll() {
        log.info("Список пользователей");
        return new ArrayList<>(users.values());
    }

    @Override
    public User get(long userId) {
        log.info("Пользователь найден");
        return users.get(userId);
    }

    @Override
    public void delete(long userId) {
        log.info("Пользователь удален");
        users.remove(userId);
    }

    private long generateId() {
        return ++id;
    }

}
