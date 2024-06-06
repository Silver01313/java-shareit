package ru.practicum.shareit.itemRequest.storage;

import ru.practicum.shareit.itemRequest.model.ItemRequest;

public interface  ItemRequestStorage {

    ItemRequest get(Long id);
}
