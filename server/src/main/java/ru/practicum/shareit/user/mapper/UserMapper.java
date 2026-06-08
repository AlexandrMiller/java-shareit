package ru.practicum.shareit.user.mapper;

import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {

    public static UserDto toUserDTO(User user) {
        return new UserDto(user.getName(), user.getEmail());
    }

    public static User toUser(UserDto dto) {
        return new User(dto.getName(),dto.getEmail());
    }
}
