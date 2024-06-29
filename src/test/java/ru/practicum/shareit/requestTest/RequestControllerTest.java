package ru.practicum.shareit.requestTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.request.controller.RequestController;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.service.RequestServiceImpl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(RequestController.class)
public class RequestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestServiceImpl requestService;

    private ObjectMapper objectMapper = new ObjectMapper();

    private RequestDto requestDto;
    private LocalDateTime created;

    @BeforeEach
    void create() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        created = LocalDateTime.of(2024, 6, 29, 11, 30);

        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("description");
        requestDto.setRequestor(1L);
        requestDto.setCreated(created);
        requestDto.setItems(new ArrayList<>());
    }

    @Test
    void createRequestTest() throws Exception {
        when(requestService.create(anyLong(), any(RequestDto.class))).thenReturn(requestDto);

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.created").value(created + ":00"))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllByRequestorTest() throws Exception {
        List<RequestDto> requestDtoList = List.of(requestDto, new RequestDto());

        when(requestService.getAllByRequestor(anyLong())).thenReturn(requestDtoList);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].requestor").value(1L))
                .andExpect(jsonPath("$[0].created").value(created + ":00"))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1]").value(new RequestDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getAllTest() throws Exception {
        List<RequestDto> requestDtoList = List.of(requestDto, new RequestDto());
        when(requestService.getAll(anyLong(), anyInt(), anyInt())).thenReturn(requestDtoList);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].description").value("description"))
                .andExpect(jsonPath("$[0].requestor").value(1L))
                .andExpect(jsonPath("$[0].created").value(created + ":00"))
                .andExpect(jsonPath("$[0].items").value(new ArrayList<>()))
                .andExpect(jsonPath("$[1]").value(new RequestDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getRequestTest() throws Exception {
        when(requestService.get(anyLong(), anyLong())).thenReturn(requestDto);

        mockMvc.perform(get("/requests/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.description").value("description"))
                .andExpect(jsonPath("$.requestor").value(1L))
                .andExpect(jsonPath("$.created").value(created + ":00"))
                .andExpect(jsonPath("$.items").value(new ArrayList<>()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
