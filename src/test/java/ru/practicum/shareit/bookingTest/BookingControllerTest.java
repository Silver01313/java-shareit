package ru.practicum.shareit.bookingTest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.controller.BookingController;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoFromFrontend;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.util.List;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BookingController.class)
public class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private BookingService bookingService;

    private ObjectMapper objectMapper;

    private BookingDto bookingDto;

    private LocalDateTime start;

    private LocalDateTime end;


    @BeforeEach
    void create() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        start = LocalDateTime.of(2024, 6, 30, 11, 30);
        end = LocalDateTime.of(2024, 6, 30, 12, 30);

        bookingDto = new BookingDto();
        bookingDto.setId(1L);
        bookingDto.setStart(start);
        bookingDto.setEnd(end);
        bookingDto.setItem(new ItemDto());
        bookingDto.setBooker(new UserDto());
        bookingDto.setStatus("APPROVED");

    }

    @Test
    void createBookingTest() throws Exception {
        when(bookingService.create(any(BookingDtoFromFrontend.class), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookingDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(start + ":00"))
                .andExpect(jsonPath("$.end").value(end + ":00"))
                .andExpect(jsonPath("$.item").value(new ItemDto()))
                .andExpect(jsonPath("$.booker").value(new UserDto()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void approveBookingTest() throws Exception {
        when(bookingService.update(anyLong(), anyLong(), anyBoolean())).thenReturn(bookingDto);

        mockMvc.perform(patch("/bookings/1?approved=true")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(start + ":00"))
                .andExpect(jsonPath("$.end").value(end + ":00"))
                .andExpect(jsonPath("$.item").value(new ItemDto()))
                .andExpect(jsonPath("$.booker").value(new UserDto()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingTest() throws Exception {
        when(bookingService.getBooking(anyLong(), anyLong())).thenReturn(bookingDto);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.start").value(start + ":00"))
                .andExpect(jsonPath("$.end").value(end + ":00"))
                .andExpect(jsonPath("$.item").value(new ItemDto()))
                .andExpect(jsonPath("$.booker").value(new UserDto()))
                .andExpect(jsonPath("$.status").value("APPROVED"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingsByUserTest() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto, new BookingDto());
        when(bookingService.findAllByBookerId(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").value(start + ":00"))
                .andExpect(jsonPath("$[0].end").value(end + ":00"))
                .andExpect(jsonPath("$[0].item").value(new ItemDto()))
                .andExpect(jsonPath("$[0].booker").value(new UserDto()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1]").value(new BookingDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }

    @Test
    void getBookingsByOwnerItemsTest() throws Exception {
        List<BookingDto> bookings = List.of(bookingDto, new BookingDto());
        when(bookingService.findAllByOwnerItems(anyLong(), anyString(), anyInt(), anyInt())).thenReturn(bookings);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "ALL")
                        .param("from", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].start").value(start + ":00"))
                .andExpect(jsonPath("$[0].end").value(end + ":00"))
                .andExpect(jsonPath("$[0].item").value(new ItemDto()))
                .andExpect(jsonPath("$[0].booker").value(new UserDto()))
                .andExpect(jsonPath("$[0].status").value("APPROVED"))
                .andExpect(jsonPath("$[1]").value(new BookingDto()))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}