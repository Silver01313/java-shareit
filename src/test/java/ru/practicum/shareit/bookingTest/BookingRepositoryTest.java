package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@DataJpaTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingRepositoryTest {

    private final BookingRepository bookingRepository;

    private final TestEntityManager em;

    User user;
    User user2;
    Item item;
    Item item2;
    Booking booking;
    Booking booking2;
    Pageable pageable;
    LocalDateTime now;

    @BeforeEach
    void create() {
        pageable = PageRequest.of(0, 10);
        now = LocalDateTime.now();

        user = new User();
        user.setName("name");
        user.setEmail("s@y.com");

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("s2@y.com");

        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);

        item2 = new Item();
        item2.setName("n2");
        item2.setDescription("d2");
        item2.setAvailable(true);
        item2.setOwner(user);

        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus("APPROVED");

        booking2 = new Booking();
        booking2.setStart(LocalDateTime.now().plusDays(3));
        booking2.setEnd(LocalDateTime.now().plusDays(4));
        booking2.setItem(item);
        booking2.setBooker(user2);
        booking2.setStatus("APPROVED");

        em.persist(user);
        em.persist(user2);
        em.persist(item);
        em.persist(item2);
        em.persist(booking);
        em.persist(booking2);
    }

    @Test
    public void shouldFindAllByBookerId() {
        List<Booking> result = bookingRepository.findAllByBookerId(user.getId(), pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);

        result = bookingRepository.findAllByBookerId(user2.getId(), pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking2);
    }

    @Test
    public void shouldFindAllCurrentByBookerId() {
        booking2.setStart(LocalDateTime.now().minusDays(1));

        List<Booking> result = bookingRepository.findAllCurrentByBookerId(user2.getId(), now, pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking2);
    }

    @Test
    public void shouldFindAllPastByBookerId() {
        booking.setEnd(LocalDateTime.now().minusDays(1));

        List<Booking> result = bookingRepository.findAllPastByBookerId(user.getId(), now, pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);
    }

    @Test
    public void shouldFindAllFutureByBookerId() {
        booking2.setBooker(user);

        List<Booking> result = bookingRepository.findAllFutureByBookerId(user.getId(), now, pageable).getContent();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), booking2);
        assertEquals(result.get(1), booking);
    }

    @Test
    public void shouldFindAllByBookerIdByStatus() {
        booking2.setBooker(user);
        booking2.setStatus("REJECTED");

        List<Booking> result = bookingRepository.findAllByBookerIdByStatus(user.getId(), "APPROVED", pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);
    }

    @Test
    public void shouldFindAllByOwnerItems() {
        booking2.setBooker(user);


        List<Booking> result = bookingRepository.findAllByOwnerItems(user.getId(), pageable).getContent();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), booking2);
        assertEquals(result.get(1), booking);
    }

    @Test
    public void shouldFindAllCurrentByOwnerItems() {
        booking2.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(1));

        List<Booking> result = bookingRepository.findAllCurrentByOwnerItems(user.getId(), now, pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);
    }

    @Test
    public void findAllPastByOwnerItems_ShouldReturnPastBookings() {
        booking2.setBooker(user);
        booking.setEnd(LocalDateTime.now().minusDays(1));

        List<Booking> result = bookingRepository.findAllPastByOwnerItems(user.getId(), now, pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);
    }

    @Test
    public void shouldFindAllFutureByOwnerItems() {
        booking2.setBooker(user);

        List<Booking> result = bookingRepository.findAllFutureByOwnerItems(user.getId(), now, pageable).getContent();

        assertEquals(result.size(), 2);
        assertEquals(result.get(0), booking2);
        assertEquals(result.get(1), booking);
    }

    @Test
    public void shouldFindAllByOwnerItemsByStatus() {
        booking2.setBooker(user);
        booking2.setStatus("REJECTED");

        List<Booking> result = bookingRepository.findAllByOwnerItemsByStatus(user.getId(), "APPROVED", pageable).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), booking);
    }

    @Test
    public void shouldGetLastBookingByItem() {
        booking2.setBooker(user);
        booking.setStart(LocalDateTime.now().minusDays(3));
        booking2.setStart(LocalDateTime.now().minusDays(2));

        Booking result = bookingRepository.getLastBookingByItem(user.getId(), now, "APPROVED");

        assertEquals(result, booking2);
    }

    @Test
    public void shouldGetNextBookingByItem() {
        booking2.setBooker(user);

        Booking result = bookingRepository.getNextBookingByItem(user.getId(), now, "APPROVED");

        assertEquals(result, booking);
    }

    @Test
    public void shouldGetBookingByBooker() {
        booking2.setBooker(user);
        booking.setEnd(LocalDateTime.now().minusDays(3));
        booking2.setEnd(LocalDateTime.now().minusDays(2));

        Booking result = bookingRepository.getBookingByBooker(item.getId(), user.getId(), now, "APPROVED");

        assertEquals(result, booking);
    }
}
