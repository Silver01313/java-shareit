package ru.practicum.shareit.request.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;


public interface RequestService {

    RequestDto create(Long requestorId, RequestDto requestDto);

    List<RequestDto> getAllByRequestor(Long requestorId);

    List<RequestDto> getAll(Long requestorId, int from, int size);

    RequestDto get(Long requestorId, Long requestId);
}
