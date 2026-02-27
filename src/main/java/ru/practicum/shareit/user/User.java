package ru.practicum.shareit.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * TODO Sprint add-controllers.
 */

@Data
public class User {

    private long id;

    @NotBlank(message = "Имя должно быть указано")
    private String name;

    @Email(message = "Некорректный email")
    @NotBlank(message = "email должен быть указан")
    private String email;

    public User(String name,String email) {
        this.name = name;
        this.email = email;
    }
}
