package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRepositoryTest {

    private final ItemRepository itemRepository;
    private final TestEntityManager em;

    User user;
    Item item;
    Item item2;
    Request request;

    @BeforeEach
    void create() {
        user = new User();
        user.setName("name");
        user.setEmail("s@y.com");

        request = new Request();
        request.setDescription("description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        item = new Item();
        item.setName("name");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);

        item2 = new Item();
        item2.setName("n2");
        item2.setDescription("d2");
        item2.setAvailable(true);
        item2.setOwner(user);

        em.persist(user);
        em.persist(request);
        em.persist(item);
        em.persist(item2);
    }

    @Test
    void shouldGetRequired() {

        List<Item> foundItems = itemRepository.getRequired("name", PageRequest.of(0, 10));

        assertEquals(foundItems.size(), 1);
        assertEquals(foundItems.get(0), item);
    }

    @Test
    void shouldFindAllByRequestId() {

        List<Item> foundItems = itemRepository.findAllByRequestId(List.of(request.getId()));

        assertEquals(foundItems.size(), 1);
        assertEquals(foundItems.get(0), item);
    }

}
