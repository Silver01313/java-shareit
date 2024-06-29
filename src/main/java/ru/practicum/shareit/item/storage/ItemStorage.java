package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {

    Item create(Long userId, ItemDto item);

    Item update(Long userId, Long itemId, ItemDto item);

    Item get(Long itemId);

    List<Item> getAll(Long userId);

    List<Item> getRequired(String query);

    List<Item> getAllItems();
}
