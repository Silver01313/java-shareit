package ru.practicum.shareit.item.storage;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class ItemStorageImpl implements ItemStorage {

    private final UserService userService;

    private final ItemRequestService itemRequestService;

    private final HashMap<Long, Item> items;

    private long id = 0;

    @Override
    public Item create(Long userId, ItemDto item) {

        User user = userService.get(userId);
        Item newItem = ItemMapper.toItem(item);

        newItem.setId(++id);
        newItem.setOwner(user);
        if (item.getRequest() != null)
            newItem.setRequest(itemRequestService.get(item.getRequest()));

        items.put(id, newItem);
        log.info("Объект создан");
        return newItem;
    }

    @Override
    public Item update(Long userId, Long itemId, ItemDto item) {

        userService.get(userId);
        Item newItem = items.get(itemId);

        if (item.getName() != null && !item.getName().isBlank())
            newItem.setName(item.getName());
        if (item.getDescription() != null && !item.getDescription().isBlank())
            newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null)
            newItem.setAvailable(item.getAvailable());

        items.put(itemId, newItem);
        log.info("Объект обновлен");
        return newItem;
    }

    @Override
    public Item get(Long itemId) {
        return items.get(itemId);
    }

    @Override
    public List<Item> getAll(Long userId) {

        User user = userService.get(userId);

        log.info("Ваш список вещей");
        return items.values().stream()
                .filter(v -> v.getOwner().equals(user))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getRequired(String query) {

        if (query.isBlank()) return new ArrayList<>();

        return  items.values().stream()
                .filter(Item::getAvailable)
                .filter(v -> v.getName().toLowerCase().contains(query.toLowerCase())
                        || v.getDescription().toLowerCase().contains(query.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getAllItems() {
        return new ArrayList<>(items.values());
    }
}
