package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {


    @NotBlank(message = "Нужно указать имя пользователя")
    private String name;

    @Email
    @NotBlank(message = "емейл должен быть указан")
    private String email;
}
