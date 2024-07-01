package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestServiceImpl requestService;

    @PostMapping
    public RequestDto create(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                             @RequestBody RequestDto requestDto) {
        return requestService.create(requestorId, requestDto);
    }

    @GetMapping
    public List<RequestDto> getAllByRequestor(@RequestHeader("X-Sharer-User-Id") Long requestorId) {
        return requestService.getAllByRequestor(requestorId);
    }

    @GetMapping("/all")
    public List<RequestDto> getAll(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                                   @RequestParam(defaultValue = "0") @PositiveOrZero int from,
                                   @RequestParam(defaultValue = "10") @Positive int size) {
        return requestService.getAll(requestorId, from, size);
    }

    @GetMapping("/{requestId}")
    public RequestDto get(@RequestHeader("X-Sharer-User-Id") Long requestorId,
                       @PathVariable Long requestId) {
        return requestService.get(requestorId, requestId);
    }
}
