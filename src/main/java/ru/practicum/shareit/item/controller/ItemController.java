package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody ItemDto item) {
        return itemService.create(userId, item);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@RequestHeader ("X-Sharer-User-Id") Long userId,
                              @PathVariable Long itemId,
                              @RequestBody ItemDto item) {
        return itemService.update(userId, itemId, item);
    }

    @GetMapping("/itemId")
    public ItemDto getItem(@PathVariable Long itemId) {
        return itemService.get(itemId);
    }

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return itemService.getAll(userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getRequiredItems(@RequestParam String query) {
        return itemService.getRequired(query);
    }

}