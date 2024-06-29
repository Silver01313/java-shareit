package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final RequestRepository requestRepository;

    @Override
    public ItemDto create(Long userId, ItemDto item) {

        checkUsrId(userId);

        if (item.getAvailable() == null) {
            log.debug("Отсутствует статус  доступности  предмета");
            throw new ValidationException("Отсутствует статус  доступности предмета");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            log.debug("Отсутствует название  предмета");
            throw new ValidationException("Отсутствует название предмета");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.debug("Отсутствует описание  предмета");
            throw new ValidationException("Отсутствует  описание предмета");
        }

        User user = userService.get(userId);
        Item newItem;

        if (item.getRequestId() != null) {
            Request request = requestRepository.findById(item.getRequestId())
                            .orElseThrow(() -> new NotFoundException("Запрос не найден"));
            newItem = ItemMapper.toItem(item, request);
        } else {
            newItem = ItemMapper.toItem(item, null);
        }

        newItem.setOwner(user);

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public CommentDto createComment(Long userId, Long itemId, CommentDto comment) {

        String status = "APPROVED";
        LocalDateTime now = LocalDateTime.now();

        checkUsrId(userId);
        if (comment.equals(new CommentDto()) || comment.getText().isBlank()) {
            log.debug("Комментарий не может быть пуст");
            throw new ValidationException("Комментарий не может быть пуст");
        }

        User user = userService.get(userId);
        Item item = getById(itemId);
        Booking booking = bookingRepository.getBookingByBooker(itemId, userId, now, status);

        if (booking == null) {
            log.debug("Бронирование не найдено");
            throw new ValidationException("Бронирование не найдено");
        }

        Comment newComment = CommentMapper.toComment(comment, item, user, now);

        return CommentMapper.toCommentDto(commentRepository.save(newComment));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {

        checkUsrId(userId);

        Item newItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (userId != newItem.getOwner().getId()) {
            log.debug("Вы не являетесь владельцем вещи");
            throw new NoAccessException("Вы не являетесь владельцем вещи");
        }

        if (item.getName() != null) {
            newItem.setName(item.getName());
        }

        if (item.getDescription() != null) {
            newItem.setDescription(item.getDescription());
        }

        if (item.getAvailable() != null) {
            newItem.setAvailable(item.getAvailable());
        }

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemWithBookingsDto get(Long itemId, Long userId) {

        Item item = getById(itemId);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(CommentMapper::toCommentDto)
                .collect(Collectors.toList());

        ItemWithBookingsDto itemDto = ItemMapper.toItemWithBookingDto(item,
                null,
                null,
                comments);


        if (item.getOwner().getId() != userId) {
            return itemDto;
        }

        String status = "APPROVED";
        LocalDateTime now = LocalDateTime.now();
        Booking lastBooking = bookingRepository.getLastBookingByItem(itemId, now, status);
        Booking nextBooking = bookingRepository.getNextBookingByItem(itemId, now, status);

        if (lastBooking != null) {
            itemDto.setLastBooking(BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                    lastBooking.getBooker().getId()));
        }

        if (nextBooking != null) {
            itemDto.setNextBooking(BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                    nextBooking.getBooker().getId()));
        }

        return itemDto;
    }

    @Override
    public List<ItemWithBookingsDto> getAllItemsByUser(Long userId, int from, int size) {

        checkUsrId(userId);

        String status = "APPROVED";
        LocalDateTime now = LocalDateTime.now();
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        List<Item> itemList = itemRepository.findAllByOwnerId(userId, pageable);

        if (itemList.isEmpty()) {
            log.debug("У вас нет вещей");
            return new ArrayList<>();
        }

        List<ItemWithBookingsDto> newList = new ArrayList<>();

        for (Item i : itemList) {
            Booking lastBooking = bookingRepository.getLastBookingByItem(i.getId(), now, status);
            Booking nextBooking = bookingRepository.getNextBookingByItem(i.getId(), now, status);
            List<CommentDto> comments = commentRepository.findAllByItemId(i.getId()).stream()
                    .map(CommentMapper::toCommentDto)
                    .collect(Collectors.toList());

            ItemWithBookingsDto item = ItemMapper.toItemWithBookingDto(i,
                    null,
                    null,
                    comments);

            if (lastBooking != null) {
                item.setLastBooking(BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                        lastBooking.getBooker().getId()));
            }

            if (nextBooking != null) {
                item.setNextBooking(BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                        nextBooking.getBooker().getId()));
            }

            newList.add(item);
        }
        return newList;
    }

    @Override
    public List<ItemDto> getRequired(String text, int from, int size) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);
        return itemRepository.getRequired(text, pageable).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует"));
    }

    private void checkUsrId(Long userId) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new ValidationException("Отсутствует идентификатор пользователя");
        }
    }
}
