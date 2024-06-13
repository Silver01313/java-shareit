package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.implementation.bytecode.Throw;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingDtoWithoutTime;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NoArgumentsException;
import ru.practicum.shareit.exception.UnsupportedStatusException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final ItemService itemService;
    private final UserService userService;

    @Override
    public BookingDto create(BookingDtoFromFrontend bookingDto, long userId) {

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = bookingDto.getStart();
        LocalDateTime end = bookingDto.getEnd();

        if (start == null) {
            log.debug("Отсутствует время начала");
            throw new NoArgumentsException("Отсутствует время начала");
        }

        if (end == null) {
            log.debug("Отсутствует время конца");
            throw new NoArgumentsException("Отсутствует время конца");
        }

        if (end.equals(start)) {
            log.debug("Время начала не может быть равно времени конца");
            throw new NoArgumentsException("Время начала не может быть равно времени конца");
        }

        if (end.isBefore(now)) {
            log.debug("Время конца не может быть в прошлом");
            throw new NoArgumentsException("Время конца не может быть в прошлом");
        }

        if (start.isBefore(now)) {
            log.debug("Время начала не может быть в будущем");
            throw new NoArgumentsException("Время начала не может быть в будущем");
        }

        if (start.isAfter(end)) {
            log.debug("Время начала не может быть после времени конца");
            throw new NoArgumentsException("Время начала не может быть после времени конца");
        }

        Item item = itemService.get(bookingDto.getItemId());

        if (item.getAvailable().equals(false)) {
            log.debug("Вещь недоступна");
            throw new NoArgumentsException("Вещь недоступна");
        }

        User user = userService.get(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(long ownerId, long bookingId, boolean approved) {

        Booking booking = bookingRepository.findById(bookingId);
        long realOwnerId = booking.getItem().getOwner().getId();

        if (realOwnerId != ownerId) {
            log.debug("Подтвердить бронирование может только владелец вещи");
            throw new NoAccessException("Подтвердить бронирование может только владелец вещи");
        }

        if (approved) booking.setStatus("APPROVED");
        if(!approved) booking.setStatus("REJECTED");

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {

        Booking booking = bookingRepository.findById(bookingId);
        User booker = booking.getBooker();
        User owner = booking.getItem().getOwner();

        if (booker.getId() != userId && owner.getId() != userId) {
            log.debug("У вас нет доступа к этому бронированию");
            throw new NoAccessException("У вас нет доступа к этому бронированию");
        }
        return BookingMapper.toBookingDto(booking);
    }


    @Override
    public List<BookingDto> findAllByBookerId(long bookerId, String param) {

        User booker = userService.get(bookerId);

        LocalDateTime now = LocalDateTime.now();

        if (param.equalsIgnoreCase("ALL")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository.findAllByBookerId(bookerId));
        }

        if (param.equalsIgnoreCase("CURRENT")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository.findAllCurrentByBookerId(bookerId, now));
        }

        if (param.equalsIgnoreCase("PAST")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository.findAllPastByBookerId(bookerId, now));
        }

        if (param.equalsIgnoreCase("FUTURE")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository.findAllFutureByBookerId(bookerId, now));
        }

        if (param.equalsIgnoreCase("WAITING")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository
                    .findAllByBookerIdByStatus(bookerId, "WAITING"));
        }

        if (param.equalsIgnoreCase("REJECTED")) {
            return BookingMapper.CollectionToBookingDto(bookingRepository
                    .findAllByBookerIdByStatus(bookerId, "REJECTED"));
        }

        log.debug("Такой параметр не поддерживается");
        throw new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    @Override
    public List<BookingDto> findAllByOwnerItems(long ownerId, String param) {

        User owner = userService.get(ownerId);

        return bookingRepository.findAllByBookerId(ownerId)
                .stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

}
