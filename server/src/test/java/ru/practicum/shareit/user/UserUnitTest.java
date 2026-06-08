package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDao;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserUnitTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserDao userDao;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Иван", "ivan@mail.ru");
    }

    // --- getUserById ---

    @Test
    void getUserById_whenExists_returnsUser() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        User result = userDao.getUserById(1L);

        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        verify(userRepository).findById(1L);
    }

    @Test
    void getUserById_whenNotExists_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userDao.getUserById(99L));
    }

    // --- createUser ---

    @Test
    void createUser_whenValid_savesAndReturnsUser() {
        when(userRepository.existsByEmailIgnoreCase(user.getEmail())).thenReturn(false);
        when(userRepository.save(user)).thenReturn(user);

        User result = userDao.createUser(user);

        assertEquals(user.getName(), result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void createUser_whenNameIsBlank_throwsValidation() {
        User invalid = new User(null, "", "test@mail.ru");

        assertThrows(ValidationException.class, () -> userDao.createUser(invalid));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenEmailIsNull_throwsValidation() {
        User invalid = new User(null, "Имя", null);

        assertThrows(ValidationException.class, () -> userDao.createUser(invalid));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenEmailDuplicate_throwsValidation() {
        when(userRepository.existsByEmailIgnoreCase(user.getEmail())).thenReturn(true);

        assertThrows(ValidationException.class, () -> userDao.createUser(user));
        verify(userRepository, never()).save(any());
    }

    @Test
    void createUser_whenUserIsNull_throwsValidation() {
        assertThrows(ValidationException.class, () -> userDao.createUser(null));
    }

    // --- deleteUser ---

    @Test
    void deleteUser_whenExists_deletesUser() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userDao.deleteUser(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteUser_whenNotExists_throwsNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> userDao.deleteUser(99L));
        verify(userRepository, never()).deleteById(any());
    }

    // --- updateUser ---

    @Test
    void updateUser_whenNewName_updatesName() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto dto = new UserDto("Новое имя", null);
        User result = userDao.updateUser(dto, 1L);

        assertEquals("Новое имя", result.getName());
        verify(userRepository).save(user);
    }

    @Test
    void updateUser_whenNewEmail_updatesEmail() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("new@mail.ru")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto dto = new UserDto(null, "new@mail.ru");
        User result = userDao.updateUser(dto, 1L);

        assertEquals("new@mail.ru", result.getEmail());
    }

    @Test
    void updateUser_whenSameEmail_doesNotCheckDuplicate() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto dto = new UserDto(null, "ivan@mail.ru");
        userDao.updateUser(dto, 1L);

        verify(userRepository, never()).existsByEmailIgnoreCase(any());
    }

    @Test
    void updateUser_whenDtoIsNull_throwsValidation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        assertThrows(ValidationException.class, () -> userDao.updateUser(null, 1L));
    }

    @Test
    void updateUser_whenUserNotFound_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userDao.updateUser(new UserDto("x", "x@x.ru"), 99L));
    }

    @Test
    void updateUser_whenDuplicateEmail_throwsValidation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmailIgnoreCase("taken@mail.ru")).thenReturn(true);

        UserDto dto = new UserDto(null, "taken@mail.ru");

        assertThrows(ValidationException.class, () -> userDao.updateUser(dto, 1L));
    }
}
