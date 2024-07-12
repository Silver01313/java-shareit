package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;

import java.util.List;

public interface BookingService {

    BookingDto create(BookingDtoFromFrontend bookingDto, long userId);

    BookingDto update(long ownerId, long bookingId, boolean approved);

    BookingDto getBooking(long userId, long bookingId);

    List<BookingDto> findAllByBookerId(long bookerId, String param, int from, int size);

    List<BookingDto> findAllByOwnerItems(long ownerId, String param, int from, int size);

}
