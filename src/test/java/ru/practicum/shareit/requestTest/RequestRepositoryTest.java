package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DataJpaTest(properties = "db.name=test")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestRepositoryTest {

    private final RequestRepository requestRepository;

    private final TestEntityManager em;

    User user;
    User user2;
    Request request;
    Request request2;

    @BeforeEach
    void create() {
        user = new User();
        user.setName("name");
        user.setEmail("s@y.com");

        user2 = new User();
        user2.setName("name2");
        user2.setEmail("s2@y.com");

        request = new Request();
        request.setDescription("description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        request2 = new Request();
        request2.setDescription("description");
        request2.setRequestor(user2);
        request2.setCreated(LocalDateTime.now());

        em.persist(user);
        em.persist(user2);
        em.persist(request);
        em.persist(request2);
    }

    @Test
    public void shouldFindAllByRequestorId() {
        List<Request> result = requestRepository.findAllByRequestorId(user.getId());

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), request);
    }

    @Test
    public void shouldFindAll() {
        PageRequest pageRequest = PageRequest.of(0, 10);
        List<Request> result = requestRepository.findAll(user.getId(), pageRequest).getContent();

        assertEquals(result.size(), 1);
        assertEquals(result.get(0), request2);
    }
}
