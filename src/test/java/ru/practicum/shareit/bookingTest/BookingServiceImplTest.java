package ru.practicum.shareit.bookingTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingServiceImplTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemService itemService;
    @Mock
    private UserService userService;
    @InjectMocks
    private BookingServiceImpl bookingService;

    private User user;
    private User user2;
    private Item item;
    private Item item2;
    private Booking nextBooking;
    private Booking lastBooking;
    private Booking currentBooking;
    private BookingDtoFromFrontend bookingDtoFromFrontend;

    @BeforeEach
    void create() {
        user = new User();
        user.setId(1L);
        user.setName("TestName");
        user.setEmail("test@example.com");

        user2 = new User();
        user2.setId(2L);
        user2.setName("TestName2");
        user2.setEmail("test@example2.com");

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(new Request());

        item2 = new Item();
        item2.setId(2L);
        item2.setName("Test Item2");
        item2.setDescription("Test Description");
        item2.setAvailable(true);
        item2.setOwner(user2);
        item2.setRequest(new Request());

        nextBooking = new Booking();
        nextBooking.setId(1L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(item2);
        nextBooking.setBooker(user);
        nextBooking.setStatus("APPROVED");

        lastBooking = new Booking();
        lastBooking.setId(2L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(item2);
        lastBooking.setBooker(user);
        lastBooking.setStatus("APPROVED");

        currentBooking = new Booking();
        currentBooking.setId(3L);
        currentBooking.setStart(LocalDateTime.now().minusDays(1));
        currentBooking.setEnd(LocalDateTime.now().plusDays(1));
        currentBooking.setItem(item2);
        currentBooking.setBooker(user);
        currentBooking.setStatus("APPROVED");

        bookingDtoFromFrontend = new BookingDtoFromFrontend();
        bookingDtoFromFrontend.setId(nextBooking.getId());
        bookingDtoFromFrontend.setStart(nextBooking.getStart());
        bookingDtoFromFrontend.setEnd(nextBooking.getEnd());
        bookingDtoFromFrontend.setItemId(nextBooking.getItem().getId());
        bookingDtoFromFrontend.setStatus(nextBooking.getStatus());

    }

    @Test
    void createShouldReturnBookingDto() {

        when(itemService.getById(item2.getId())).thenReturn(item2);
        when(userService.get(user.getId())).thenReturn(user);
        when(bookingRepository.save(any(Booking.class))).thenReturn(nextBooking);

        BookingDto result = bookingService.create(bookingDtoFromFrontend, user.getId());

        assertNotNull(result);
        assertEquals(result.getId(), bookingDtoFromFrontend.getId());
        assertEquals(result.getStart(), bookingDtoFromFrontend.getStart());
        assertEquals(result.getEnd(), bookingDtoFromFrontend.getEnd());
        assertEquals(result.getItem().getId(), nextBooking.getItem().getId());
        assertEquals(result.getBooker().getId(), nextBooking.getBooker().getId());
        assertEquals(result.getStatus(), nextBooking.getStatus());

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createShouldThrowExceptionWhenNoValidate() {
        bookingDtoFromFrontend = new BookingDtoFromFrontend();
        bookingDtoFromFrontend.setId(nextBooking.getId());
        bookingDtoFromFrontend.setEnd(nextBooking.getEnd());
        bookingDtoFromFrontend.setItemId(nextBooking.getItem().getId());
        bookingDtoFromFrontend.setStatus(nextBooking.getStatus());
        LocalDateTime now = LocalDateTime.now();

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setStart(nextBooking.getStart());
        bookingDtoFromFrontend.setEnd(null);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setEnd(nextBooking.getStart());

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setEnd(now.minusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setEnd(nextBooking.getEnd());
        bookingDtoFromFrontend.setStart(now.minusDays(1));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setStart(now.plusDays(3));

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        bookingDtoFromFrontend.setStart(nextBooking.getStart());

        when(itemService.getById(item.getId())).thenReturn(item);

        bookingDtoFromFrontend.setItemId(item.getId());

        assertThrows(NotFoundException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

        when(itemService.getById(item2.getId())).thenReturn(item2);
        bookingDtoFromFrontend.setItemId(nextBooking.getItem().getId());
        item2.setAvailable(false);

        assertThrows(ValidationException.class, () -> bookingService.create(bookingDtoFromFrontend, user.getId()));

    }

    @Test
    void updateShouldReturnApprovedOrRejectedBooking() {
        nextBooking.setStatus("null");
        when(bookingRepository.findById(nextBooking.getId())).thenReturn(Optional.of(nextBooking));
        when(bookingRepository.save(any(Booking.class))).thenReturn(nextBooking);

        BookingDto positiverResult = bookingService.update(user2.getId(), nextBooking.getId(), true);

        assertNotNull(positiverResult);
        assertEquals(positiverResult.getId(), nextBooking.getId());
        assertEquals(positiverResult.getStart(), nextBooking.getStart());
        assertEquals(positiverResult.getEnd(), nextBooking.getEnd());
        assertEquals(positiverResult.getItem().getId(), nextBooking.getItem().getId());
        assertEquals(positiverResult.getBooker().getId(), nextBooking.getBooker().getId());
        assertEquals(positiverResult.getBooker().getId(), nextBooking.getBooker().getId());
        assertEquals(positiverResult.getStatus(), "APPROVED");

        nextBooking.setStatus("null");
        BookingDto negativeResult = bookingService.update(user2.getId(), nextBooking.getId(), false);

        assertNotNull(positiverResult);
        assertEquals(negativeResult.getStatus(), "REJECTED");
    }

    @Test
    void updateShouldThrowExceptionWhenNoValidate() {
        when(bookingRepository.findById(nextBooking.getId())).thenReturn(Optional.of(nextBooking));

        assertThrows(ValidationException.class,
                () -> bookingService.update(user2.getId(), nextBooking.getId(), false));

        nextBooking.setStatus("REJECTED");

        assertThrows(ValidationException.class,
                () -> bookingService.update(user2.getId(), nextBooking.getId(), false));

        nextBooking.setStatus("null");

        assertThrows(NotFoundException.class,
                () -> bookingService.update(user.getId(), nextBooking.getId(), false));
    }

    @Test
    void getBookingShouldThrowExceptionWhenNoValidate() {
        when(bookingRepository.findById(99L)).thenThrow(new NotFoundException("Бронирование не найдено"));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(user.getId(), 99L));

        when(bookingRepository.findById(nextBooking.getId())).thenReturn(Optional.of(nextBooking));

        assertThrows(NotFoundException.class, () -> bookingService.getBooking(99L, nextBooking.getId()));
    }

    @Test
    void findAllByBookerIdShouldReturnListOfBookingDto() {
        when(userService.get(user.getId())).thenReturn(user);

        List<Booking> bookings = List.of(lastBooking, currentBooking, nextBooking);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllByBookerId(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "ALL", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "ALL", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));
        assertEquals(bookingService.findAllByBookerId(user.getId(), "ALL", 0, 10).get(1),
                BookingMapper.toBookingDto(bookings.get(1)));
        assertEquals(bookingService.findAllByBookerId(user.getId(), "ALL", 0, 10).get(2),
                BookingMapper.toBookingDto(bookings.get(2)));

        bookings = List.of(currentBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllCurrentByBookerId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "CURRENT", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "CURRENT", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        bookings = List.of(lastBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllPastByBookerId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "PAST", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "PAST", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        bookings = List.of(nextBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllFutureByBookerId(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "FUTURE", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "FUTURE", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        nextBooking.setStatus("WAITING");
        when(bookingRepository.findAllByBookerIdByStatus(user.getId(), "WAITING", pageRequest))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "WAITING", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "WAITING", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        nextBooking.setStatus("REJECTED");
        when(bookingRepository.findAllByBookerIdByStatus(user.getId(), "REJECTED", pageRequest))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByBookerId(user.getId(), "REJECTED", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByBookerId(user.getId(), "REJECTED", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        assertThrows(ValidationException.class, () -> bookingService.findAllByBookerId(user.getId(),
                "UNSUPPORTED", 0, 10).get(0));
    }

    @Test
    void findAllByOwnerItemsShouldReturnListOfBookingDto() {
        item2.setOwner(user);

        when(userService.get(user.getId())).thenReturn(user);

        List<Booking> bookings = List.of(lastBooking, currentBooking, nextBooking);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Booking> bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllByOwnerItems(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "ALL", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "ALL", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "ALL", 0, 10).get(1),
                BookingMapper.toBookingDto(bookings.get(1)));
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "ALL", 0, 10).get(2),
                BookingMapper.toBookingDto(bookings.get(2)));

        bookings = List.of(currentBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllCurrentByOwnerItems(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "CURRENT", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "CURRENT", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        bookings = List.of(lastBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllPastByOwnerItems(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "PAST", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "PAST", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        bookings = List.of(nextBooking);
        bookingsPage = new PageImpl<>(bookings, pageRequest, bookings.size());

        when(bookingRepository.findAllFutureByOwnerItems(anyLong(), any(LocalDateTime.class), any(PageRequest.class)))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "FUTURE", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "FUTURE", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        nextBooking.setStatus("WAITING");
        when(bookingRepository.findAllByOwnerItemsByStatus(user.getId(), "WAITING", pageRequest))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "WAITING", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "WAITING", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        nextBooking.setStatus("REJECTED");
        when(bookingRepository.findAllByOwnerItemsByStatus(user.getId(), "REJECTED", pageRequest))
                .thenReturn(bookingsPage);

        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "REJECTED", 0, 10).size(),
                bookings.size());
        assertEquals(bookingService.findAllByOwnerItems(user.getId(), "REJECTED", 0, 10).get(0),
                BookingMapper.toBookingDto(bookings.get(0)));

        assertThrows(ValidationException.class, () -> bookingService.findAllByOwnerItems(user.getId(),
                "UNSUPPORTED", 0, 10).get(0));
    }
}
