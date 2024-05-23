package ru.practicum.shareit.request.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;

@Component
public class ItemRequestStorageImpl implements ItemRequestStorage {

    private HashMap<Long, ItemRequest> requests;

    public ItemRequest getRequest(Long id) {
        return requests.get(id);
    }
}
