package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    Item get(Long itemId);

    List<ItemDto> getAllItemsByUser(Long userId);

    List<ItemDto> getRequired(String query);
}
