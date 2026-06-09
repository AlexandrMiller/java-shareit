package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
@Service
public class UserDao implements UserInterface {

    private final UserRepository userRepository;

    public UserDao(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public User getUserById(Long id) {

        return userRepository.findById(id).orElseThrow(() -> new NotFoundException("Пользователь не найден"));
    }


    @Transactional
    public User createUser(User user) {
        log.info("Запрос на создание пользователя");
        validateUserDto(user);
        checkEmail(user.getEmail());
        userRepository.save(user);
        log.info("Запрос выполнен");
        return user;
    }

    public void checkEmail(String email) {

        boolean exists = userRepository.existsByEmailIgnoreCase(email);

        if (exists) {
            throw new ValidationException("Пользователь с таким email уже существует");
        }
    }

    @Transactional
    public void deleteUser(long id) {

        if (!userRepository.existsById(id)) {
            throw new NotFoundException("Такого пользователья не существует");
        }
        userRepository.deleteById(id);
        log.info("User deleted");
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

    @Transactional
    public User updateUser(UserDto dto, long userId) {

        User user = getUserById(userId);

        if (dto == null) {
            throw new ValidationException("Ничего не указано для изменений");
        }

        if (dto.getName() != null && !dto.getName().isBlank()) {
            user.setName(dto.getName());
        }

        if (dto.getEmail() != null && !dto.getEmail().isBlank()) {

            if (!dto.getEmail().equalsIgnoreCase(user.getEmail())) {
                checkEmail(dto.getEmail());
                user.setEmail(dto.getEmail());
            }
        }

        userRepository.save(user);

        return user;
    }
}
