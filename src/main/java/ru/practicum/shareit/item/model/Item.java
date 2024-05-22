package ru.practicum.shareit.item.model;

import lombok.*;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@RequiredArgsConstructor
public class Item {
    private long id;
    @NonNull
    @NotBlank
    private String name;
    @NonNull
    @NotBlank
    private String description;
    @NonNull
    private Boolean available;
    private User owner;
    private ItemRequest request;

    public boolean isAvailable() {
        return available;
    }
}
