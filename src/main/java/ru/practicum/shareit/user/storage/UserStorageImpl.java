package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.AlreadyExistsException;
import ru.practicum.shareit.exception.NotFoundException;
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
        if (isUserAlreadyExist(user)) {
            log.debug("Пользователь с таким email уже существует");
            throw new AlreadyExistsException("Пользователь с таким email уже существует");
        }
        user.setId(generateId());
        users.put(user.getId(), user);
        log.info("Пользователь добавлен");
        return user;
    }

    @Override
    public User update(User user, Long userId) {
        if (!users.containsKey(userId)) {
            log.debug("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }

        User newUser = users.get(userId);

        if (user.getName() != null) {
            newUser.setName(user.getName());
        }

        if(user.getEmail() != null) {
            users.remove(userId);
            if (isUserAlreadyExist(user)) {
                users.put(userId,newUser);
                log.debug("Пользователь с таким email уже существует");
                throw new AlreadyExistsException("Пользователь с таким email уже существует");
            }
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
        if (!users.containsKey(userId)) {
            log.debug("Такого пользователя не существует");
            throw new NotFoundException("Такого пользователя не существует");
        }
        log.info("Пользователь найден");
        return users.get(userId);
    }

    @Override
    public void remove(long userId) {
        log.info("Пользователь удален");
        users.remove(userId);
    }

    private long generateId() {
        return ++id;
    }

    private boolean isUserAlreadyExist(User user) {
        return users.values().stream()
                .map(User::getEmail)
                .anyMatch(email -> email.equals(user.getEmail()));
    }

}
