package ru.practicum.shareit.requestTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.request.service.RequestServiceImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestServiceImplTest {

    @Mock
    private RequestRepository requestRepository;
    @Mock
    private UserService userService;
    @Mock
    private ItemRepository itemRepository;
    @InjectMocks
    private RequestServiceImpl requestService;
    private User user;
    private Item item;
    private Request request;
    private RequestDto requestDto;

    @BeforeEach
    void create() {
        user = new User();
        user.setId(1L);
        user.setName("TestName");
        user.setEmail("test@example.com");

        request = new Request();
        request.setId(1L);
        request.setDescription("description");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        requestDto = new RequestDto();
        requestDto.setId(1L);
        requestDto.setDescription("description");
        requestDto.setRequestor(user.getId());

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);

    }

    @Test
    void createShouldReturnRequestDto() {
        when(userService.get(user.getId())).thenReturn(user);
        when(requestRepository.save(any(Request.class))).thenReturn(request);

        RequestDto result = requestService.create(user.getId(), requestDto);

        assertNotNull(result);
        assertEquals(result.getId(), request.getId());
        assertEquals(result.getDescription(), request.getDescription());
        assertEquals(result.getRequestor(), request.getRequestor().getId());
        assertEquals(result.getItems(), new ArrayList<>());
        assertNotNull(result.getCreated());

        verify(requestRepository, times(1)).save(any(Request.class));

        requestDto.setDescription("");

        assertThrows(ValidationException.class, () -> requestService.create(user.getId(), requestDto));
    }

    @Test
    void getAllByRequestorShouldReturnListOfRequestDto() {
        when(userService.get(anyLong())).thenReturn(user);
        when(requestRepository.findAllByRequestorId(anyLong())).thenReturn(List.of(request));
        when(itemRepository.findAllByRequestId(List.of(1L))).thenReturn(List.of(item));

        List<RequestDto> result = requestService.getAllByRequestor(user.getId());

        assertNotNull(result);
        assertEquals(result.get(0).getId(), requestDto.getId());
        assertEquals(result.get(0).getDescription(), requestDto.getDescription());
        assertEquals(result.get(0).getRequestor(), requestDto.getRequestor());
        assertNotNull(result.get(0).getItems());
        assertNotNull(result.get(0).getCreated());
    }

    @Test
    void getAllShouldReturnListOfRequestDto() {
        List<Request> requests = List.of(request);
        PageRequest pageRequest = PageRequest.of(0, 10);
        Page<Request> requestsPage = new PageImpl<>(requests, pageRequest, requests.size());

        when(userService.get(anyLong())).thenReturn(user);
        when(requestRepository.findAll(anyLong(), any(Pageable.class))).thenReturn(requestsPage);
        when(itemRepository.findAllByRequestId(List.of(1L))).thenReturn(List.of(item));

        List<RequestDto> result = requestService.getAll(user.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), requestDto.getId());
        assertEquals(result.get(0).getDescription(), requestDto.getDescription());
        assertEquals(result.get(0).getRequestor(), requestDto.getRequestor());
        assertNotNull(result.get(0).getItems());
        assertNotNull(result.get(0).getCreated());
    }

    @Test
    void getShouldReturnRequestDto() {
        when(userService.get(anyLong())).thenReturn(user);
        when(requestRepository.findById(1L)).thenReturn(Optional.of(request));
        when(itemRepository.findAllByRequestId(List.of(1L))).thenReturn(List.of(item));

        RequestDto result = requestService.get(user.getId(), request.getId());

        assertNotNull(result);
        assertEquals(result.getId(), requestDto.getId());
        assertEquals(result.getDescription(), requestDto.getDescription());
        assertEquals(result.getRequestor(), requestDto.getRequestor());
        assertNotNull(result.getItems());
        assertNotNull(result.getCreated());

        when(requestRepository.findById(99L)).thenThrow(new NotFoundException("Запрос не найден"));

        assertThrows(NotFoundException.class, () -> requestService.get(1L, 99L));
        assertThrows(ValidationException.class, () -> requestService.get(null, 1L));
    }
}
