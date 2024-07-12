package ru.practicum.shareit.itemTest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserService userService;

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private RequestRepository requestRepository;

    @InjectMocks
    private ItemServiceImpl itemService;

    private User user;
    private Item item;
    private Request request;
    private Booking lastBooking;
    private Booking nextBooking;
    private Comment comment;

    @BeforeEach
    void create() {
        user = new User();
        user.setId(1L);
        user.setName("TestName");
        user.setEmail("test@example.com");

        request = new Request();
        request.setId(1L);
        request.setDescription("Test Request");
        request.setRequestor(user);
        request.setCreated(LocalDateTime.now());

        item = new Item();
        item.setId(1L);
        item.setName("Test Item");
        item.setDescription("Test Description");
        item.setAvailable(true);
        item.setOwner(user);
        item.setRequest(request);

        lastBooking = new Booking();
        lastBooking.setId(1L);
        lastBooking.setStart(LocalDateTime.now().minusDays(2));
        lastBooking.setEnd(LocalDateTime.now().minusDays(1));
        lastBooking.setItem(item);
        lastBooking.setBooker(user);
        lastBooking.setStatus("APPROVED");

        nextBooking = new Booking();
        nextBooking.setId(2L);
        nextBooking.setStart(LocalDateTime.now().plusDays(1));
        nextBooking.setEnd(LocalDateTime.now().plusDays(2));
        nextBooking.setItem(item);
        nextBooking.setBooker(user);
        nextBooking.setStatus("APPROVED");

        comment = new Comment();
        comment.setText("comment");
        comment.setItem(item);
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void createShouldReturnItemDto() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Test Item");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        when(requestRepository.findById(itemDto.getRequestId())).thenReturn(Optional.of(request));
        when(userService.get(user.getId())).thenReturn(user);
        when(itemRepository.save(any(Item.class))).thenReturn(item);
        ItemDto result = itemService.create(user.getId(), itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(item.getName(), result.getName());
        assertEquals(item.getDescription(), result.getDescription());
        assertEquals(item.getAvailable(), result.getAvailable());
        assertEquals(item.getRequest().getId(), result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void shouldThrowExceptionWhenItemNotValid() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("");
        itemDto.setDescription("Test Description");
        itemDto.setAvailable(true);

        assertThrows(ValidationException.class, () -> itemService.create(1L, itemDto));

        itemDto.setName("a");
        itemDto.setDescription("");

        assertThrows(ValidationException.class, () -> itemService.create(1L, itemDto));

        itemDto.setDescription("b");
        itemDto.setAvailable(null);

        assertThrows(ValidationException.class, () -> itemService.create(1L, itemDto));
    }

    @Test
    void shouldThrowExceptionIfCommentIsEmpty() {
        CommentDto comment = new CommentDto();
        assertThrows(ValidationException.class, () -> itemService.createComment(user.getId(), item.getId(), comment));
    }

    @Test
    void updateShouldChangeExistingItem() {
        ItemDto itemDto = new ItemDto();
        itemDto.setName("Updated Item");
        itemDto.setDescription("Updated Description");
        itemDto.setAvailable(true);
        itemDto.setRequestId(request.getId());

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).thenReturn(item);

        ItemDto result = itemService.update(user.getId(), item.getId(), itemDto);

        assertNotNull(result);
        assertEquals(item.getId(), result.getId());
        assertEquals(itemDto.getName(), result.getName());
        assertEquals(itemDto.getDescription(), result.getDescription());
        assertEquals(itemDto.getAvailable(), result.getAvailable());
        assertEquals(itemDto.getRequestId(), result.getRequestId());

        verify(itemRepository, times(1)).save(any(Item.class));
    }

    @Test
    void updateShouldThrowExceptions() {
        ItemDto itemDto = new ItemDto();

        when(itemRepository.findById(99L)).thenThrow(new NotFoundException(""));
        assertThrows(NotFoundException.class, () -> itemService.update(user.getId(), 99L, itemDto));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        assertThrows(NotFoundException.class, () -> itemService.update(2L, item.getId(), itemDto));
    }

    @Test
    void getShouldReturnItemWithBookingDto() {
        ItemWithBookingsDto itemDto = ItemMapper.toItemWithBookingDto(item,
                null,
                null,
                List.of(CommentMapper.toCommentDto(comment)));

        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        ItemWithBookingsDto result = itemService.get(item.getId(), 2L);

        assertNotNull(result);
        assertEquals(result.getId(), itemDto.getId());
        assertEquals(result.getName(), itemDto.getName());
        assertEquals(result.getDescription(), itemDto.getDescription());
        assertEquals(result.getAvailable(), itemDto.getAvailable());
        assertEquals(result.getRequest(), itemDto.getRequest());
        assertEquals(result.getComments().get(0), itemDto.getComments().get(0));

        when(bookingRepository.getLastBookingByItem(anyLong(), any(), anyString()))
                .thenReturn(lastBooking);
        when(bookingRepository.getNextBookingByItem(anyLong(), any(), anyString()))
                .thenReturn(nextBooking);

        itemDto.setLastBooking(BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                lastBooking.getBooker().getId()));
        itemDto.setNextBooking(BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                nextBooking.getBooker().getId()));

        result = itemService.get(item.getId(), 1L);

        assertNotNull(result);
        assertEquals(result.getLastBooking(), itemDto.getLastBooking());
        assertEquals(result.getNextBooking(), itemDto.getNextBooking());
    }

    @Test
    void getShouldReturnListOfItemWithBookingDto() {
        ItemWithBookingsDto itemDto = ItemMapper.toItemWithBookingDto(item,
                null,
                null,
                List.of(CommentMapper.toCommentDto(comment)));
        itemDto.setLastBooking(BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                lastBooking.getBooker().getId()));
        itemDto.setNextBooking(BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                nextBooking.getBooker().getId()));

        when(itemRepository.findAllByOwnerId(anyLong(), any())).thenReturn(List.of(item));
        when(bookingRepository.getLastBookingByItem(anyLong(), any(), anyString()))
                .thenReturn(lastBooking);
        when(bookingRepository.getNextBookingByItem(anyLong(), any(), anyString()))
                .thenReturn(nextBooking);
        when(commentRepository.findAllByItemId(item.getId())).thenReturn(List.of(comment));

        List<ItemWithBookingsDto> result = itemService.getAllItemsByUser(user.getId(), 0, 10);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), itemDto.getId());
        assertEquals(result.get(0).getName(), itemDto.getName());
        assertEquals(result.get(0).getDescription(), itemDto.getDescription());
        assertEquals(result.get(0).getAvailable(), itemDto.getAvailable());
        assertEquals(result.get(0).getRequest(), itemDto.getRequest());
        assertEquals(result.get(0).getComments().get(0), itemDto.getComments().get(0));
        assertEquals(result.get(0).getLastBooking(), itemDto.getLastBooking());
        assertEquals(result.get(0).getNextBooking(), itemDto.getNextBooking());
    }

    @Test
    void getRequiredShouldReturnListOfItemDto() {
        ItemDto itemDto = ItemMapper.toItemDto(item);

        when(itemRepository.getRequired(anyString(), any())).thenReturn(List.of(item));

        List<ItemDto> result = itemService.getRequired("text", 0, 10);

        assertNotNull(result);
        assertEquals(result.get(0).getId(), itemDto.getId());
        assertEquals(result.get(0).getName(), itemDto.getName());
        assertEquals(result.get(0).getDescription(), itemDto.getDescription());
        assertEquals(result.get(0).getAvailable(), itemDto.getAvailable());
        assertEquals(result.get(0).getRequestId(), itemDto.getRequestId());
        assertEquals(result.get(0).getRequestId(), itemDto.getRequestId());
    }
}
