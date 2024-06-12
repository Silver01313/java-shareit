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
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;

    public ItemDto create(Long userId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        if (item.getAvailable() == null) {
            log.debug("Отсутствует статус  доступности  предмета");
            throw new NoArgumentsException("Отсутствует статус  доступности предмета");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            log.debug("Отсутствует название  предмета");
            throw new NoArgumentsException("Отсутствует название предмета");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.debug("Отсутствует описание  предмета");
            throw new NoArgumentsException("Отсутствует  описание предмета");
        }
        User user = userService.get(userId);
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        Item newItem = itemRepository.findById(itemId)
                .orElseThrow(()-> new NotFoundException("Вещь не найдена"));

        if (userId != newItem.getOwner().getId()) {
            log.debug("Вы не являетесь владельцем вещи");
            throw new NoAccessException("Вы не являетесь владельцем вещи");
        }

        if (item.getName() != null) newItem.setName(item.getName());
        if(item.getDescription() != null) newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) newItem.setAvailable(item.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    public Item get(Long itemId) {
    /*    if (!itemRepository.findAll().contains(itemRepository.findById(itemId))) {
            log.debug("Такой вещи не существует");
            throw new NotFoundException("Такой вещи не существует");
        }*/

        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует"));
    }

    public List<ItemDto> getAllItemsByUser(Long userId) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        return itemRepository.findAllByOwnerId(userId).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }

    public List<ItemDto> getRequired(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.getRequired(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
