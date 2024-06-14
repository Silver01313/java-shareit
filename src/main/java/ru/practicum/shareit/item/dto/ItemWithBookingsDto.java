package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;

@Data
@AllArgsConstructor
public class ItemWithBookingsDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingWithIdAndBookerId lastBooking;
    private BookingWithIdAndBookerId nextBooking;


    public boolean isAvailable() {
        return available;
    }
}
