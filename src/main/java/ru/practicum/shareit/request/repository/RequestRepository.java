package ru.practicum.shareit.request.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.shareit.request.model.Request;

import java.awt.print.Pageable;
import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Request save (Request request);

    Request findById(long requestId);

    @Query("Select r from Request r " +
            "WHERE r.requestor.id = :requestorId " +
            "ORDER BY r.created DESC")
    List<Request> findAllByRequestorId(@Param("requestorId") Long requestorId);

    @Query("Select r from Request r " +
            "WHERE r.requestor.id != :requestorId " +
            "ORDER BY r.created DESC")
    List<Request> findAll(@Param("requestorId") Long requestorId);
}
