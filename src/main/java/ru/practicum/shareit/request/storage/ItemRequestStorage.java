package ru.practicum.shareit.request.storage;

import org.apache.coyote.Request;
import ru.practicum.shareit.request.model.ItemRequest;

public interface  ItemRequestStorage {
    ItemRequest getRequest(Long id);
}
