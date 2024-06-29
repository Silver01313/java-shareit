package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;

public interface BookingRepository extends JpaRepository<Booking, Long> {

    Booking save(Booking booking);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.booker bk " +
                    "WHERE bk.id = ?1 ")
    Page<Booking> findAllByBookerId(long bookerId, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.booker bk " +
                    "WHERE bk.id = ?1 " +
                    "AND b.start < ?2 ")
    Page<Booking> findAllCurrentByBookerId(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "SELECT b FROM Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.end < ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "WHERE b.booker.id = ?1 " +
                    "AND b.end < ?2")
    Page<Booking> findAllPastByBookerId(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.booker bk " +
                    "WHERE bk.id = ?1 " +
                    "AND b.start > ?2 ")
    Page<Booking> findAllFutureByBookerId(long bookerId, LocalDateTime now, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE bk.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.booker bk " +
                    "WHERE bk.id = ?1 " +
                    "AND b.status = ?2 ")
    Page<Booking> findAllByBookerIdByStatus(long bookerId, String status, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "WHERE i.owner.id = ?1 ")
    Page<Booking> findAllByOwnerItems(long ownerId, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start < ?2 " +
            "AND b.end > ?2 " +
            "ORDER BY b.start ",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "WHERE i.owner.id = ?1 " +
                    "AND b.start < ?2 " +
                    "AND b.end > ?2 ")
    Page<Booking> findAllCurrentByOwnerItems(long ownerId, LocalDateTime now, Pageable pageable);

  @Query(value = "SELECT b FROM Booking b " +
          "JOIN FETCH b.item i " +
          "JOIN FETCH b.booker bk " +
          "WHERE i.owner.id = ?1 " +
          "AND b.end < ?2 " +
          "ORDER BY b.start DESC",
          countQuery = "SELECT count(b) FROM Booking b " +
                  "JOIN b.item i " +
                  "JOIN i.owner o " +
                  "WHERE b.item.owner.id = ?1 " +
                  "AND b.end < ?2")
    Page<Booking> findAllPastByOwnerItems(long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.start > ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
                    "JOIN b.item i " +
                    "WHERE i.owner.id = ?1 " +
                    "AND b.start > ?2 ")
    Page<Booking> findAllFutureByOwnerItems(long ownerId, LocalDateTime now, Pageable pageable);

    @Query(value = "Select b from Booking b " +
            "JOIN FETCH b.item i " +
            "JOIN FETCH b.booker bk " +
            "WHERE i.owner.id = ?1 " +
            "AND b.status = ?2 " +
            "ORDER BY b.start DESC",
            countQuery = "SELECT count(b) FROM Booking b " +
            "JOIN b.item i " +
                    "WHERE i.owner.id = ?1 " +
                    "AND b.status = ?2 ")
    Page<Booking> findAllByOwnerItemsByStatus(long ownerId, String status, Pageable pageable);

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
            "ORDER BY b.end_time " +
            "LIMIT 1", nativeQuery = true)
    Booking getBookingByBooker(long itemId, long bookerId, LocalDateTime now, String status);
}
