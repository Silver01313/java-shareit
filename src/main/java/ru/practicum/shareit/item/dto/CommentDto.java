package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class CommentDto {

    private long id;
    private String text;
    private long itemId;
    private long authorId;
}
