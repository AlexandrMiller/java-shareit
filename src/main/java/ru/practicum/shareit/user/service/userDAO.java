package ru.practicum.shareit.user.service;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDAO;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDTO;

import java.util.*;

@Repository
public class userDAO {

    Map<Long, User> users = new HashMap<>();


    private long userId = 0;




    public User getUserById(Long id) {

        if (!users.containsKey(id)) {
            throw new NotFoundException("Такого пользователя не существует");
        }
        return users.get(id);
    }


    public Map<Long, User> getAllUsers() {
         return users;
    }


    public User createUser(User user) {
        validateUserDto(user);
        checkEmail(user.getEmail());
        user.setId(getNextId());
        users.put(user.getId(),user);
        return user;
    }


    public long getNextId() {
        return ++userId;
    }


    public void checkEmail(String email) {

        boolean exists = users.values().stream()
                .anyMatch(user -> user.getEmail().equalsIgnoreCase(email));

        if (exists) {
            throw new RuntimeException("Пользователь с таким email уже существует");
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
            throw new NotFoundException("Нужно указать пользователя");
        }

        if (user.getName() == null || user.getName().isBlank()) {
            throw new NotFoundException("Нужно указать имя пользователя");
        }

        if (user.getEmail() == null || user.getEmail().isBlank()) {
            throw new NotFoundException("Email не указан");
        }
    }

    public User updateUser(UserDTO dto,long userId) {

        User user = getUserById(userId);

        if (dto == null) {
            throw new NotFoundException("Ничего не указано для изменений");
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
