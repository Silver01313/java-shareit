package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingArgument;
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
import java.util.Objects;

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
            log.debug("Время начала не может быть в прошлом");
            throw new ValidationException("Время начала не может быть в прошлом");
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

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

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

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование не найдено"));

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
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        Page<Booking> bookingsPage;
        BookingArgument constParam = BookingArgument.getParam(param);

        if (constParam == null) {
            log.debug("Такой параметр не поддерживается");
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

            switch (constParam) {
                case ALL:
                    bookingsPage = bookingRepository.findAllByBookerId(bookerId, pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                case CURRENT:
                    bookingsPage = bookingRepository.findAllCurrentByBookerId(bookerId, now, pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                case PAST:
                    bookingsPage = bookingRepository.findAllPastByBookerId(bookerId, now, pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                case FUTURE:
                    bookingsPage = bookingRepository.findAllFutureByBookerId(bookerId, now, pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                case WAITING:
                    bookingsPage = bookingRepository.findAllByBookerIdByStatus(bookerId, "WAITING", pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                case REJECTED:
                    bookingsPage = bookingRepository.findAllByBookerIdByStatus(bookerId, "REJECTED", pageable);
                    return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

                default:
                    log.debug("Такой параметр не поддерживается");
                    throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
            }
    }


    @Override
    public List<BookingDto> findAllByOwnerItems(long ownerId, String param, int from, int size) {

        User owner = userService.get(ownerId);
        LocalDateTime now = LocalDateTime.now();
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(from, size);
        Page<Booking> bookingsPage;
        BookingArgument constParam = BookingArgument.getParam(param);

        if (constParam == null) {
            log.debug("Такой параметр не поддерживается");
            throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }

        switch (constParam) {
            case ALL:
                bookingsPage = bookingRepository.findAllByOwnerItems(ownerId, pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            case CURRENT:
                bookingsPage = bookingRepository.findAllCurrentByOwnerItems(ownerId, now, pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            case PAST:
                bookingsPage = bookingRepository.findAllPastByOwnerItems(ownerId, now, pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            case FUTURE:
                bookingsPage = bookingRepository.findAllFutureByOwnerItems(ownerId, now, pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            case WAITING:
                bookingsPage = bookingRepository.findAllByOwnerItemsByStatus(ownerId, "WAITING", pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            case REJECTED:
                bookingsPage = bookingRepository.findAllByOwnerItemsByStatus(ownerId, "REJECTED", pageable);
                return BookingMapper.collectionToBookingDto(bookingsPage.getContent());

            default:
                log.debug("Такой параметр не поддерживается");
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
    }
}
