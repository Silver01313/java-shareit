package ru.practicum.shareit.itemRequest.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.itemRequest.model.ItemRequest;
import ru.practicum.shareit.itemRequest.storage.ItemRequestStorage;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;

    public ItemRequest get(Long id) {
        return itemRequestStorage.get(id);
    }
}
