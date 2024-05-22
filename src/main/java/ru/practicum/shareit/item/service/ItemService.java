package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto item);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    ItemDto get(Long itemId);

    List<ItemDto> getAll(Long userId);

    List<ItemDto> getRequired(String query);
}
