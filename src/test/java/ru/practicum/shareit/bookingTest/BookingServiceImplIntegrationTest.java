package ru.practicum.shareit.bookingTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceImplIntegrationTest {

    private final EntityManager em;
    private final BookingServiceImpl service;

    @Test
    public void createShouldSaveInDatabase() {
        User user = new User();
        user.setName("И");
        user.setEmail("i@e.com");

        User user2 = new User();
        user2.setName("И");
        user2.setEmail("i2@e.com");

        Item item = new Item();
        item.setName("item");
        item.setDescription("Description");
        item.setAvailable(true);
        item.setOwner(user);

        em.persist(user);
        em.persist(user2);
        em.persist(item);

        BookingDtoFromFrontend bookingDto = new BookingDtoFromFrontend();
        bookingDto.setStart(LocalDateTime.now().plusDays(1));
        bookingDto.setEnd(LocalDateTime.now().plusDays(2));
        bookingDto.setItemId(item.getId());

        BookingDto createdBooking = service.create(bookingDto, user2.getId());

        assertNotNull(createdBooking);
        assertEquals(createdBooking.getItem().getId(), item.getId());
        assertEquals(createdBooking.getBooker().getId(), user2.getId());
        assertEquals(createdBooking.getStart(), bookingDto.getStart());
        assertEquals(createdBooking.getEnd(), bookingDto.getEnd());

        Booking savedBooking = em.find(Booking.class, createdBooking.getId());

        assertNotNull(savedBooking);
        assertEquals(createdBooking.getId(), savedBooking.getId());
        assertEquals(createdBooking.getStart(), savedBooking.getStart());
        assertEquals(createdBooking.getEnd(), savedBooking.getEnd());
        assertEquals(savedBooking.getItem(), item);
        assertEquals(savedBooking.getBooker(), user2);
    }
}
