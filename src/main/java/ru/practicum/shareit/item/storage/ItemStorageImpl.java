package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NoArgumentsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.storage.ItemRequestStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.HashMap;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage{

    private final UserStorage userStorage;

    private final ItemRequestStorage itemRequestStorage;

    private final HashMap<Long, Item> items;

    private long id = 0;

    public Item create(Long userId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        User user = userStorage.get(userId);
        if (item.getAvailable() == null) {
            log.debug("Отсутствует название предмета");
            throw new NoArgumentsException("Отсутствует название предмета");
        }
        if (item.getName() == null || item.getName().isBlank()) {
            log.debug("Отсутствует описание предмета");
            throw new NoArgumentsException("Отсутствует описание предмета");
        }
        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.debug("Отсутствует статус доступности предмета");
            throw new NoArgumentsException("Отсутствует статус доступности предмета");
        }

        Item newItem = ItemMapper.toItem(item);
        newItem.setId(++id);
        newItem.setOwner(user);
        if (item.getRequest() != null) newItem.setRequest(itemRequestStorage.getRequest(item.getRequest()));
        items.put(id, newItem);
        log.info("Объект создан");
        return newItem;
    }

    public Item update(Long userId, Long itemId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        if(!items.containsKey(itemId)) {
            log.debug("Такой вещи не существует");
            throw new NotFoundException("Такой вещи не существует");
        }

        User user = userStorage.get(userId);
        Item newItem = items.get(itemId);

        if (userId != newItem.getOwner().getId()) {
            log.debug("Вы не являетесь владельцем вещи");
            throw new NoAccessException("Вы не являетесь владельцем вещи");
        }

        if (item.getName() != null && !item.getName().isBlank()) newItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) newItem.setAvailable(item.getAvailable());

        items.put(itemId, newItem );

        return newItem;
    }

    public Item get(Long itemId) {
        return null;
    }

    public List<Item> getAll(Long userId) {
        return null;
    }

    public List<Item> getRequired(String query) {
        return null;
    }

}
