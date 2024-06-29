package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemWithBookingsDto {

    private long id;
    private String name;
    private String description;
    private Boolean available;
    private Long request;
    private BookingWithIdAndBookerId lastBooking;
    private BookingWithIdAndBookerId nextBooking;
    private List<CommentDto> comments;

}
