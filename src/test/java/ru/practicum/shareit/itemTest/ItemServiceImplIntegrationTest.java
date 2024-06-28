package ru.practicum.shareit.itemTest;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
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
public class ItemServiceImplIntegrationTest {

    private final EntityManager em;
    private final ItemServiceImpl itemService;
    private final CommentRepository commentRepository;
    User newUser;

    @BeforeEach
    void create() {
        newUser = new User();
        newUser.setName("И");
        newUser.setEmail("i@e.com");
    }
    @Test
    void createShouldSaveInDatabase() {

        ItemDto newItemDto = new ItemDto();
        newItemDto.setName("Н");
        newItemDto.setDescription("Н");
        newItemDto.setAvailable(true);

        em.persist(newUser);

        ItemDto createdItem = itemService.create(newUser.getId(), newItemDto);

        assertEquals(newItemDto.getName(), createdItem.getName());
        assertEquals(newItemDto.getDescription(), createdItem.getDescription());
        assertTrue(createdItem.isAvailable());

        Item savedItem = em.find(Item.class, createdItem.getId());

        assertNotNull(savedItem);
        assertEquals(savedItem.getId(), createdItem.getId());
        assertEquals(savedItem.getName(), createdItem.getName());
        assertEquals(savedItem.getDescription(), createdItem.getDescription());
        assertEquals(savedItem.getAvailable(), createdItem.getAvailable());
        assertEquals(savedItem.getOwner(), newUser);
    }

    @Test
    void createCommentShouldSaveInDatabase() {
        Item item = new Item();
        item.setName("item");
        item.setDescription("description");
        item.setAvailable(true);
        item.setOwner(newUser);

        em.persist(newUser);
        em.persist(item);

        CommentDto commentDto = new CommentDto();
        commentDto.setText("s");

        Comment comment = CommentMapper.toComment(commentDto, item, newUser, LocalDateTime.now());

        Comment createdComment= commentRepository.save(comment);

        assertNotNull(createdComment);
        assertEquals(createdComment.getId(), comment.getId());

        Comment savedComment = em.find(Comment.class, createdComment.getId());

        assertNotNull(savedComment);
        assertEquals(savedComment.getId(), createdComment.getId());
        assertEquals(savedComment.getText(), createdComment.getText());
        assertEquals(savedComment.getItem(), createdComment.getItem());
        assertEquals(savedComment.getItem(), createdComment.getItem());
        assertEquals(savedComment.getAuthor(), createdComment.getAuthor());
        assertEquals(savedComment.getCreated(), createdComment.getCreated());
    }
}
