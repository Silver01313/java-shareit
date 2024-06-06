package ru.practicum.shareit.itemRequest.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ItemRequest {

    private long id;
    @NonNull
    private String description;
    @NonNull
    private User requestor;
    @NonNull
    private LocalDateTime created;
}
