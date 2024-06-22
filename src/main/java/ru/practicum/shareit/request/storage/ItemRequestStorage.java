package ru.practicum.shareit.request.storage;

import ru.practicum.shareit.request.model.Request;

public interface  ItemRequestStorage {

    Request get(Long id);
}
