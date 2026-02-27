package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDTO;

public class UserMapper {

    public static UserDTO toUserDTO(User user) {
        return new UserDTO(user.getName(), user.getEmail());
    }

    public static User toUser(UserDTO dto) {
        return new User(dto.getName(),dto.getEmail());
    }
}
