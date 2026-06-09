package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

public interface UserInterface {

    User createUser(User user);

    void deleteUser(long id);

    User updateUser(UserDto dto, long userId);


}
