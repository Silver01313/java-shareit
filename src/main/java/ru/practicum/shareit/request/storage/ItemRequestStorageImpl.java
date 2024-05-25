package ru.practicum.shareit.request.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class ItemRequestStorageImpl implements ItemRequestStorage {


    private  final HashMap<Long, ItemRequest> requests;

    public ItemRequest get(Long id) {
        return requests.get(id);
    }
}
