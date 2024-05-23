package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    public ItemDto create(Long userId, ItemDto item) {
        return ItemMapper.toItemDto(itemStorage.create(userId, item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        return ItemMapper.toItemDto(itemStorage.update(userId, itemId, item));
    }

    public ItemDto get(Long itemId) {
        return ItemMapper.toItemDto(itemStorage.get(itemId));
    }

    public List<ItemDto> getAll(Long userId) {
        return itemStorage.getAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> getRequired(String query) {
        return itemStorage.getRequired(query).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
