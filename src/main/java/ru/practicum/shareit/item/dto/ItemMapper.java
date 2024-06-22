package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.Request;

import java.util.List;

public class ItemMapper {

    public static ItemDto toItemDto(Item item) {
        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null
                );
    }

    public static Item toItem(ItemDto itemDto, Request request) {
        Item item = new Item();
        item.setName(itemDto.getName());
        item.setDescription(itemDto.getDescription());
        item.setAvailable(itemDto.isAvailable());
        item.setRequest(request);
        return item;
    }

    public static ItemWithBookingsDto toItemWithBookingDto(Item item,
                                                           BookingWithIdAndBookerId lastBooking,
                                                           BookingWithIdAndBookerId nextBooking,
                                                           List<CommentDto> comments) {
        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking,
                comments
        );
    }
}
