package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

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
            throw new ValidationException("Отсутствует время начала");
        }

        if (end == null) {
            log.debug("Отсутствует время конца");
            throw new ValidationException("Отсутствует время конца");
        }

        if (end.equals(start)) {
            log.debug("Время начала не может быть равно времени конца");
            throw new ValidationException("Время начала не может быть равно времени конца");
        }

        if (end.isBefore(now)) {
            log.debug("Время конца не может быть в прошлом");
            throw new ValidationException("Время конца не может быть в прошлом");
        }

        if (start.isBefore(now)) {
            log.debug("Время начала не может быть в будущем");
            throw new ValidationException("Время начала не может быть в будущем");
        }

        if (start.isAfter(end)) {
            log.debug("Время начала не может быть после времени конца");
            throw new ValidationException("Время начала не может быть после времени конца");
        }

        Item item = itemService.getById(bookingDto.getItemId());

        if (item.getAvailable().equals(false)) {
            log.debug("Вещь недоступна");
            throw new ValidationException("Вещь недоступна");
        }

        if (item.getOwner().getId() == userId) {
            log.debug("Бронирование недоступно для владельца");
            throw new NotFoundException("Бронирование недоступно для владельца");
        }

        User user = userService.get(userId);
        Booking booking = BookingMapper.toBooking(bookingDto, user, item);

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto update(long ownerId, long bookingId, boolean approved) {

        if (bookingRepository.findById(bookingId) == null) {
            log.debug("Бронирование не найдено");
            throw new NotFoundException("Бронирование не найдено");
        }

        Booking booking = bookingRepository.findById(bookingId);

        if (booking.getStatus().equals("APPROVED") || booking.getStatus().equals("REJECTED")) {
            log.debug("Подверждение уже прошло");
            throw new ValidationException("Подверждение уже прошло");
        }

        long realOwnerId = booking.getItem().getOwner().getId();

        if (realOwnerId != ownerId) {
            log.debug("Подтвердить бронирование может только владелец вещи");
            throw new NotFoundException("Подтвердить бронирование может только владелец вещи");
        }

        if (approved) {
            booking.setStatus("APPROVED");
        }

        if (!approved) {
            booking.setStatus("REJECTED");
        }

        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBooking(long userId, long bookingId) {

        if (bookingRepository.findById(bookingId) == null) {
            log.debug("Бронирование не найдено");
            throw new NotFoundException("Бронирование не найдено");
        }

        Booking booking = bookingRepository.findById(bookingId);

        User booker = booking.getBooker();
        User owner = booking.getItem().getOwner();

        if (booker.getId() != userId && owner.getId() != userId) {
            log.debug("У вас нет доступа к этому бронированию");
            throw new NotFoundException("У вас нет доступа к этому бронированию");
        }

        return BookingMapper.toBookingDto(booking);
    }


    @Override
    public List<BookingDto> findAllByBookerId(long bookerId, String param, int from, int size) {

        User booker = userService.get(bookerId);
        LocalDateTime now = LocalDateTime.now();

        switch (param) {
            case ("ALL"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllByBookerId(bookerId));

            case ("CURRENT"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllCurrentByBookerId(bookerId, now));

            case ("PAST"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllPastByBookerId(bookerId, now));

            case ("FUTURE"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllFutureByBookerId(bookerId, now));

            case ("WAITING"):
                return BookingMapper.collectionToBookingDto(bookingRepository
                        .findAllByBookerIdByStatus(bookerId, "WAITING"));

            case ("REJECTED"):
                return BookingMapper.collectionToBookingDto(bookingRepository
                        .findAllByBookerIdByStatus(bookerId, "REJECTED"));

            default:
                log.debug("Такой параметр не поддерживается");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }

    @Override
    public List<BookingDto> findAllByOwnerItems(long ownerId, String param, int from, int size) {

        User owner = userService.get(ownerId);
        LocalDateTime now = LocalDateTime.now();

        switch (param) {
            case ("ALL"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllByOwnerItems(ownerId));

            case ("CURRENT"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllCurrentByOwnerItems(ownerId, now));

            case ("PAST"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllPastByOwnerItems(ownerId, now));

            case ("FUTURE"):
                return BookingMapper.collectionToBookingDto(bookingRepository.findAllFutureByOwnerItems(ownerId, now));

            case ("WAITING"):
                return BookingMapper.collectionToBookingDto(bookingRepository
                        .findAllByOwnerItemsByStatus(ownerId, "WAITING"));

            case ("REJECTED"):
                return BookingMapper.collectionToBookingDto(bookingRepository
                        .findAllByOwnerItemsByStatus(ownerId, "REJECTED"));

            default:
                log.debug("Такой параметр не поддерживается");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
