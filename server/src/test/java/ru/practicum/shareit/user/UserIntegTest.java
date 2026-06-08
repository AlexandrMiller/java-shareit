package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDao;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class UserIntegTest {

    private final UserDao userDao;

    @Test
    void createUser_andGetById_returnsCorrectUser() {
        User created = userDao.createUser(new User(null, "Алексей", "alex@mail.ru"));

        User found = userDao.getUserById(created.getId());

        assertThat(found.getName(), equalTo("Алексей"));
        assertThat(found.getEmail(), equalTo("alex@mail.ru"));
    }

    @Test
    void createUser_withDuplicateEmail_throwsValidation() {
        userDao.createUser(new User(null, "Первый", "dup@mail.ru"));

        assertThrows(ValidationException.class,
                () -> userDao.createUser(new User(null, "Второй", "dup@mail.ru")));
    }

    @Test
    void deleteUser_removesUser() {
        User created = userDao.createUser(new User(null, "Удалить", "del@mail.ru"));
        Long id = created.getId();

        userDao.deleteUser(id);

        assertThrows(NotFoundException.class, () -> userDao.getUserById(id));
    }

    @Test
    void deleteUser_whenNotExists_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> userDao.deleteUser(999L));
    }

    @Test
    void updateUser_updatesNameAndEmail() {
        User created = userDao.createUser(new User(null, "Старое", "old@mail.ru"));

        User updated = userDao.updateUser(new UserDto("Новое", "new@mail.ru"), created.getId());

        assertThat(updated.getName(), equalTo("Новое"));
        assertThat(updated.getEmail(), equalTo("new@mail.ru"));
    }

    @Test
    void updateUser_withDuplicateEmail_throwsValidation() {
        userDao.createUser(new User(null, "Занятый", "busy@mail.ru"));
        User target = userDao.createUser(new User(null, "Цель", "target@mail.ru"));

        assertThrows(ValidationException.class,
                () -> userDao.updateUser(new UserDto(null, "busy@mail.ru"), target.getId()));
    }

    @Test
    void getUserById_whenNotExists_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> userDao.getUserById(999L));
    }
}
