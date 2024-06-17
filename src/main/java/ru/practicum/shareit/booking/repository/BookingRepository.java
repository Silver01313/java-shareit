package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    Booking findById(long bookingId);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerId(long bookerId);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllCurrentByBookerId(long bookerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastByBookerId(long bookerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureByBookerId(long bookerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByBookerIdByStatus(long bookerId, String status);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerItems(long ownerId);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start ")
    List<Booking> findAllCurrentByOwnerItems(long ownerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllPastByOwnerItems(long ownerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllFutureByOwnerItems(long ownerId, LocalDateTime now);

    @Query("Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC")
    List<Booking> findAllByOwnerItemsByStatus(long ownerId, String status);

    @Query(value = "Select b.* from Bookings b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_time < ?2 " +
            "AND b.status = ?3 " +
            "ORDER BY b.start_time DESC " +
            "LIMIT 1", nativeQuery = true)
    Booking getLastBookingByItem(long itemId, LocalDateTime now, String status);

    @Query(value = "Select b.* from Bookings b " +
            "WHERE b.item_id = ?1 " +
            "AND b.start_time > ?2 " +
            "AND b.status = ?3 " +
            "ORDER BY b.start_time " +
            "LIMIT 1", nativeQuery = true)
    Booking getNextBookingByItem(long itemId, LocalDateTime now, String status);

    @Query(value = "Select b.* from Bookings b " +
            "WHERE b.item_id = ?1 " +
            "AND b.booker_id = ?2 " +
            "AND b.end_time < ?3 " +
            "AND b.status = ?4 " +
            "ORDER BY b.start_time " +
            "LIMIT 1", nativeQuery = true)
    Booking getBookingByBooker(long itemId, long bookerId, LocalDateTime now, String status);
}
