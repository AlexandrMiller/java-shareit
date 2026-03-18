package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserDao;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserDao userDao;

    public UserController(UserDao userDao) {
        this.userDao = userDao;
    }

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Request body: {}", user);
       User user1 = userDao.createUser(user);
        log.info("User created: {}", user);
        return user1;
    }

    @GetMapping("/{id}")
    public User getUser(@PathVariable long id) {
        return userDao.getUserById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteUser(@PathVariable long id) {
        userDao.deleteUser(id);
    }

    @PatchMapping("/{id}")
    public User updateUser(@PathVariable long id, @RequestBody UserDto dto) {
        return userDao.updateUser(dto, id);
    }
}
