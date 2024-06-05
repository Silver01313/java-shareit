package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.storage.ItemRequestStorage;

@Service
@RequiredArgsConstructor
public class ItemRequestService {

    private final ItemRequestStorage itemRequestStorage;

    public ItemRequest get(Long id) {
        return itemRequestStorage.get(id);
    }
}
