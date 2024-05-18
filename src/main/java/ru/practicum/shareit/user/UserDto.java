package ru.practicum.shareit.user;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
public class UserDto {

    private long id;
    @NotNull
    private String name;
    @NotNull
    @Email
    private String email;

}
