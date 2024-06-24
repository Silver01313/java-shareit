package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {

    Item save(Item item);

    Item findById(long itemId);

    List<Item> findAllByOwnerId(long ownerId, Pageable pageable);

    @Query(" select i from Item i " +
            "where (upper(i.name) like upper(concat('%', ?1, '%')) " +
            " or upper(i.description) like upper(concat('%', ?1, '%'))) " +
            "and i.available = true")
    List<Item> getRequired(String text, Pageable pageable);

    @Query("Select i from Item i " +
            "WHERE i.request.id IN ?1 ")
    List<Item> findAllByRequestId(List<Long> requestsId);

}
