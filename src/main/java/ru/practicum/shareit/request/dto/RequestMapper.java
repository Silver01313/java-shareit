package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

public class RequestMapper {
    public static Request toRequest(RequestDto requestDto, User requestor) {
        Request request = new Request();
        request.setDescription(requestDto.getDescription());
        request.setRequestor(requestor);
        request.setCreated(LocalDateTime.now());
        return request;
    }

    public static RequestDto toRequestDto(Request request, List<ItemDto> items) {
        RequestDto requestDto = new RequestDto();
        requestDto.setId(request.getId());
        requestDto.setDescription(request.getDescription());
        requestDto.setRequestor(request.getRequestor().getId());
        requestDto.setCreated(request.getCreated());
        requestDto.setItems(items);
        return requestDto;
    }
}
