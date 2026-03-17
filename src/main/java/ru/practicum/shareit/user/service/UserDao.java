package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.*;

@Slf4j
@Repository
public class UserDao {

    Map<Long, User> users = new HashMap<>();

    private long userId = 0;


    public User getUserById(Long id) {

        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return users.get(id);
    }


    public User createUser(User user) {
        log.info("Запрос на создание пользователя");
        validateUserDto(user);
        checkEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(),user);
        log.info("Запрос выполнен");
        return user;
    }


    public long getNextId() {
        return ++userId;
    }


    public void checkEmail(String email) {

        boolean exists = users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (exists) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }
    }

    public void deleteUser(long id) {

        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с ID " + id + " не найден");
        }
        users.remove(id);
    }

    public void validateUserDto(User user) {

        if (user == null) {
            throw new ValidationException("Нужно указать пользователя");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new ValidationException("Нужно указать имя пользователя");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new ValidationException("Email не указан");
        }
    }

    public User updateUser(UserDto dto, long userId) {

        User user = getUserById(userId);

        if (dto == null) {
            throw new ValidationException("Ничего не указано для изменений");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {
            checkEmail(dto.getEmail());
            user.setEmail(dto.getEmail());
        }

        users.put(user.getId(),user);

        return user;
    }
}
