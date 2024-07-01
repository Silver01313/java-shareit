package ru.practicum.shareit.itemTest;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.dto.BookingWithIdAndBookerId;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemWithBookingsDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
public class ItemDtoJsonTest {

    @Autowired
    private JacksonTester<ItemWithBookingsDto> json;

    @Test
    void testItemDto() throws Exception {

        ItemWithBookingsDto itemDto = new ItemWithBookingsDto();
        itemDto.setId(1L);
        itemDto.setName("name");
        itemDto.setDescription("description");
        itemDto.setAvailable(true);
        itemDto.setRequest(1L);

        BookingWithIdAndBookerId lastBooking = new BookingWithIdAndBookerId();
        lastBooking.setId(2L);
        itemDto.setLastBooking(lastBooking);

        BookingWithIdAndBookerId nextBooking = new BookingWithIdAndBookerId();
        nextBooking.setId(3L);
        itemDto.setNextBooking(nextBooking);

        List<CommentDto> comments = new ArrayList<>();
        CommentDto comment = new CommentDto();
        comment.setId(4L);
        comments.add(comment);
        itemDto.setComments(comments);

        JsonContent<ItemWithBookingsDto> result = json.write(itemDto);

        assertThat(result).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(result).extractingJsonPathStringValue("$.name").isEqualTo("name");
        assertThat(result).extractingJsonPathStringValue("$.description").isEqualTo("description");
        assertThat(result).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(result).extractingJsonPathNumberValue("$.request").isEqualTo(1);
        assertThat(result).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(2);
        assertThat(result).extractingJsonPathNumberValue("$.nextBooking.id").isEqualTo(3);
        assertThat(result).extractingJsonPathNumberValue("$.comments[0].id").isEqualTo(4);
    }
}
