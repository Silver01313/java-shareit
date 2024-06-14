package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.NoAccessException;
import ru.practicum.shareit.exception.NoArgumentsException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;
    private final UserService userService;
    private final BookingRepository bookingRepository;

    @Override
    public ItemDto create(Long userId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        if (item.getAvailable() == null) {
            log.debug("Отсутствует статус  доступности  предмета");
            throw new NoArgumentsException("Отсутствует статус  доступности предмета");
        }

        if (item.getName() == null || item.getName().isBlank()) {
            log.debug("Отсутствует название  предмета");
            throw new NoArgumentsException("Отсутствует название предмета");
        }

        if (item.getDescription() == null || item.getDescription().isBlank()) {
            log.debug("Отсутствует описание  предмета");
            throw new NoArgumentsException("Отсутствует  описание предмета");
        }
        User user = userService.get(userId);
        Item newItem = ItemMapper.toItem(item);
        newItem.setOwner(user);
        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemDto update(Long userId, Long itemId, ItemDto item) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        Item newItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Вещь не найдена"));

        if (userId != newItem.getOwner().getId()) {
            log.debug("Вы не являетесь владельцем вещи");
            throw new NoAccessException("Вы не являетесь владельцем вещи");
        }

        if (item.getName() != null) newItem.setName(item.getName());
        if (item.getDescription() != null) newItem.setDescription(item.getDescription());
        if (item.getAvailable() != null) newItem.setAvailable(item.getAvailable());

        return ItemMapper.toItemDto(itemRepository.save(newItem));
    }

    @Override
    public ItemWithBookingsDto get(Long itemId, Long userId) {

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует"));

        if (item.getOwner().getId() != userId) {
            return ItemMapper.toItemWithBookingDto(item, null, null);
        }

        String status = "APPROVED";
        LocalDateTime now =LocalDateTime.now();


        /*List<Booking> lastBookings = bookingRepository.getLastBookingByItem(itemId, now, status);
        List<Booking> nextBookings = bookingRepository.getNextBookingByItem(itemId, now, status);

        if (lastBookings.isEmpty() && nextBookings.isEmpty()) {
            return ItemMapper.toItemWithBookingDto(item, null, null);
        }

        if (lastBookings.isEmpty()) {
            return ItemMapper.toItemWithBookingDto(item,
                    null,
                    BookingMapper.toBookingWithIdAndBookerId(nextBookings.get(0).getId(),
                            nextBookings.get(0).getBooker().getId()));
        }

        if (nextBookings.isEmpty()) {
            return ItemMapper.toItemWithBookingDto(item,
                    BookingMapper.toBookingWithIdAndBookerId(lastBookings.get(0).getId(),
                            lastBookings.get(0).getBooker().getId()),
                    null);
        }

        return ItemMapper.toItemWithBookingDto(item,
                BookingMapper.toBookingWithIdAndBookerId(lastBookings.get(0).getId(),
                        lastBookings.get(0).getBooker().getId()),
                BookingMapper.toBookingWithIdAndBookerId(nextBookings.get(0).getId(),
                        nextBookings.get(0).getBooker().getId()));*/

        List<Booking>lastBookings = bookingRepository.getLastBookingsByItem(itemId, now, status);
        List<Booking>nextBookings = bookingRepository.getNextBookingsByItem(itemId, now, status);

        Booking lastBooking = bookingRepository.getLastBookingByItem(itemId, now, status);
        Booking nextBooking = bookingRepository.getNextBookingByItem(itemId, now, status);

        if (lastBooking == null && nextBooking == null ) {
            return ItemMapper.toItemWithBookingDto(item, null, null);
        }

        if (lastBooking == null) {
            return ItemMapper.toItemWithBookingDto(item,
                    null,
                    BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                            nextBooking.getBooker().getId()));
        }

        if (nextBooking == null) {
            return ItemMapper.toItemWithBookingDto(item,
                    BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                            lastBooking.getBooker().getId()),
                    null);
        }

        return ItemMapper.toItemWithBookingDto(item,
                BookingMapper.toBookingWithIdAndBookerId(lastBooking.getId(),
                        lastBooking.getBooker().getId()),
                BookingMapper.toBookingWithIdAndBookerId(nextBooking.getId(),
                        nextBooking.getBooker().getId()));

    }

    @Override
    public Item getById(Long itemId) {
        return itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException("Такой вещи не существует"));
    }

    @Override
    public List<ItemWithBookingsDto> getAllItemsByUser(Long userId) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new NoArgumentsException("Отсутствует идентификатор пользователя");
        }

        List<Item> itemList = itemRepository.findAllByOwnerId(userId);
        return itemRepository.findAllByOwnerId(userId).stream()
                .map(item -> ItemMapper.toItemWithBookingDto(item, null, null))
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getRequired(String text) {
        if (text.isBlank()) return Collections.emptyList();
        return itemRepository.getRequired(text).stream().map(ItemMapper::toItemDto).collect(Collectors.toList());
    }
}
