package ru.practicum.shareit.itemTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;
import ru.practicum.shareit.item.controller.ItemController;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ItemController.class)
public class ItemControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ItemService itemService;

    private ObjectMapper objectMapper;

    private ItemDto newItem;
    private ItemWithBookingsDto itemWithBookingsDto;

    @BeforeEach
    void create() {
        objectMapper = new ObjectMapper();

        newItem = new ItemDto();
        newItem.setId(1L);
        newItem.setName("name");
        newItem.setDescription("description");
        newItem.setAvailable(true);
        newItem.setRequestId(1L);

        itemWithBookingsDto = new ItemWithBookingsDto();
        itemWithBookingsDto.setId(1L);
        itemWithBookingsDto.setName("name");
        itemWithBookingsDto.setDescription("description");
        itemWithBookingsDto.setAvailable(true);
        itemWithBookingsDto.setRequest(1L);
        itemWithBookingsDto.setLastBooking(new BookingWithIdAndBookerId());
        itemWithBookingsDto.setNextBooking(new BookingWithIdAndBookerId());
        itemWithBookingsDto.setComments(new ArrayList<>());
    }

    @Test
    void createItemTest() throws Exception {

        when(itemService.create(any(Long.class), any(ItemDto.class))).thenReturn(newItem);

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void createCommentTest() throws Exception {
        CommentDto newComment = new CommentDto();
        newComment.setId(1L);
        newComment.setText("text");
        newComment.setAuthorName("author");

        when(itemService.createComment(any(Long.class), any(Long.class), any(CommentDto.class))).thenReturn(newComment);

        mockMvc.perform(post("/items/1/comment")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newComment)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.text").value("text"))
                .andExpect(jsonPath("$.authorName").value("author"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void updateItemTest() throws Exception {
        ItemDto updatedItem = new ItemDto();
        updatedItem.setId(1L);
        updatedItem.setName("name");
        updatedItem.setDescription("description");
        updatedItem.setAvailable(true);
        updatedItem.setRequestId(1L);

        when(itemService.update(eq(1L), eq(1L), any(ItemDto.class))).thenReturn(updatedItem);

        mockMvc.perform(patch("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatedItem)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.requestId").value(1L))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getItemTest() throws Exception {
        when(itemService.get(1L, 1L)).thenReturn(itemWithBookingsDto);

        mockMvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("name"))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.available").value(true))
                .andExpect(jsonPath("$.request").value(1L))
                .andExpect(jsonPath("$.lastBooking").value(new BookingWithIdAndBookerId()))
                .andExpect(jsonPath("$.nextBooking").value(new BookingWithIdAndBookerId()))
                .andExpect(jsonPath("$.comments").value(new ArrayList<>()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getItemsTest() throws Exception {
        List<ItemWithBookingsDto> itemsList = List.of(itemWithBookingsDto, new ItemWithBookingsDto());

        when(itemService.getAllItemsByUser(anyLong(), anyInt(), anyInt())).thenReturn(itemsList);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].request").value(1L))
                .andExpect(jsonPath("$[0].lastBooking").value(new BookingWithIdAndBookerId()))
                .andExpect(jsonPath("$[0].nextBooking").value(new BookingWithIdAndBookerId()))
                .andExpect(jsonPath("$[0].comments").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1]").value(new ItemWithBookingsDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getRequiredItemsTest() throws Exception {
        List<ItemDto> requiredItems = List.of(newItem, new ItemDto());

        when(itemService.getRequired(any(String.class), anyInt(), anyInt())).thenReturn(requiredItems);

        mockMvc.perform(get("/items/search?text=someText")
                        .header("X-Sharer-User-Id", 1)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("name"))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].available").value(true))
                .andExpect(jsonPath("$[0].requestId").value(1L))
                .andExpect(jsonPath("$[1]").value(new ItemDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}