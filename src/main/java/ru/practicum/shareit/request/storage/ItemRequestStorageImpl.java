package ru.practicum.shareit.request.storage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.request.model.Request;

import java.util.HashMap;

@Component
@RequiredArgsConstructor
public class ItemRequestStorageImpl implements ItemRequestStorage {


    private  final HashMap<Long, Request> requests;

    public Request get(Long id) {
        return requests.get(id);
    }
}
