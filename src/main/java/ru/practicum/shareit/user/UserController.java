package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDTO;
import ru.practicum.shareit.user.service.userDAO;

import java.util.List;
import java.util.Map;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final userDAO userDao;

    public UserController(userDAO userDao) {
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
    public User updateUser(@PathVariable long id, @RequestBody UserDTO dto) {
        return userDao.updateUser(dto, id);
    }
}
