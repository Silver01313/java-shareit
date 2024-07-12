package ru.practicum.shareit.requestTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@Transactional
@SpringBootTest(
        properties = "db.name = test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RequestServiceImplIntegrationTest {

    private final EntityManager em;
    private final RequestServiceImpl service;

    @Test
    void createShouldSaveInDatabase() {
        User newUser = new User();
        newUser.setName("И");
        newUser.setEmail("i@e.com");

        em.persist(newUser);

        RequestDto requestDto = new RequestDto();
        requestDto.setDescription("s");

        RequestDto createdRequest = service.create(newUser.getId(), requestDto);

        assertNotNull(createdRequest);
        assertEquals(createdRequest.getDescription(), requestDto.getDescription());
        assertEquals(createdRequest.getItems(), new ArrayList<>());

        Request savedRequest = em.find(Request.class, createdRequest.getId());

        assertNotNull(savedRequest);
        assertEquals(savedRequest.getId(), createdRequest.getId());
        assertEquals(savedRequest.getDescription(), createdRequest.getDescription());
        assertEquals(savedRequest.getDescription(), createdRequest.getDescription());
        assertEquals(savedRequest.getRequestor().getId(), createdRequest.getRequestor());
        assertEquals(savedRequest.getCreated(), createdRequest.getCreated());
    }
}
