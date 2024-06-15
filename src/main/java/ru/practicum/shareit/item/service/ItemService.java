package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(Long userId, ItemDto item);

    CommentDto createComment(Long userId, Long itemId, CommentDto comment);

    ItemDto update(Long userId, Long itemId, ItemDto item);

    ItemWithBookingsDto get(Long itemId, Long userId);

    Item getById(Long itemId);

    List<ItemWithBookingsDto> getAllItemsByUser(Long userId);

    List<ItemDto> getRequired(String query);
}
