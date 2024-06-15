package ru.practicum.shareit.item.dto;

import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;
import ru.practicum.shareit.item.model.Item;

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

    public static Item toItem(ItemDto itemDto) {
        return new Item(
                itemDto.getName(),
                itemDto.getDescription(),
                itemDto.isAvailable()
        );
    }

    public static ItemWithBookingsDto toItemWithBookingDto(Item item,
                                                           BookingWithIdAndBookerId lastBooking,
                                                           BookingWithIdAndBookerId nextBooking) {
        return new ItemWithBookingsDto(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.isAvailable(),
                item.getRequest() != null ? item.getRequest().getId() : null,
                lastBooking,
                nextBooking
        );
    }
}
