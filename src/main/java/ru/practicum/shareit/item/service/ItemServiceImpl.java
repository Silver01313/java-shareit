package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NoArgumentsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    public ItemDto create(Long userId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

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

        return ItemMapper.toItemDto(itemStorage.create(userId, item));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        Item newItem = get(itemId);

        if (userId != newItem.getOwner().getId()) {
            log.debug("Вы не являетесь владельцем вещи");
            throw new NoAccessException("Вы не являетесь владельцем вещи");
        }

        return ItemMapper.toItemDto(itemStorage.update(userId, itemId, item));
    }

    public Item get(Long itemId) {
        if (!itemStorage.getAllItems().contains(itemStorage.get(itemId))) {
            log.debug("Такой вещи не существует");
            throw new NotFoundException("Такой вещи не существует");
        }

        return itemStorage.get(itemId);
    }

    public List<ItemDto> getAll(Long userId) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        return itemStorage.getAll(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> getRequired(String query) {
        return itemStorage.getRequired(query).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
