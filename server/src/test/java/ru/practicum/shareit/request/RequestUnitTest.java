package ru.practicum.shareit.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestDao;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RequestUnitTest {

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RequestMapper requestMapper;

    @InjectMocks
    private RequestDao requestDao;

    private User user;
    private ItemRequest itemRequest;
    private ItemRequestDto itemRequestDto;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Иван", "ivan@mail.ru");
        itemRequest = new ItemRequest(1L, "Нужна дрель", LocalDateTime.now(), user);
        itemRequestDto = new ItemRequestDto(1L, "Нужна дрель", itemRequest.getCreated(), List.of());
    }

    @Test
    void createRequest_whenUserExists_savesAndReturnsRequest() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        ItemRequest result = requestDao.createRequest(new ItemRequestDto(null, "Нужна дрель", null, null), 1L);

        assertEquals(itemRequest.getDescription(), result.getDescription());
        assertEquals(user, result.getRequestor());
        verify(requestRepository).save(any(ItemRequest.class));
    }

    @Test
    void createRequest_whenUserNotFound_throwsNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> requestDao.createRequest(new ItemRequestDto(null, "текст", null, null), 99L));
        verify(requestRepository, never()).save(any());
    }

    @Test
    void getItemRequestsByRequestorId_whenUserExists_returnsDtoList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of(itemRequest));
        when(requestMapper.toItemRequestDtoWithItems(itemRequest)).thenReturn(itemRequestDto);

        List<ItemRequestDto> result = requestDao.getItemRequestsByRequestorId(1L);

        assertEquals(1, result.size());
        assertEquals("Нужна дрель", result.get(0).getDescription());
    }

    @Test
    void getItemRequestsByRequestorId_whenUserNotFound_throwsNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class,
                () -> requestDao.getItemRequestsByRequestorId(99L));
        verify(requestRepository, never()).findByRequestorIdOrderByCreatedDesc(any());
    }

    @Test
    void getItemRequestsByRequestorId_whenNoRequests_returnsEmptyList() {
        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findByRequestorIdOrderByCreatedDesc(1L)).thenReturn(List.of());

        List<ItemRequestDto> result = requestDao.getItemRequestsByRequestorId(1L);

        assertTrue(result.isEmpty());
    }

    @Test
    void getAllRequests_returnsOtherUsersRequests() {
        User other = new User(2L, "Другой", "other@mail.ru");
        ItemRequest otherRequest = new ItemRequest(2L, "Нужен молоток", LocalDateTime.now(), other);
        ItemRequestDto otherDto = new ItemRequestDto(2L, "Нужен молоток", otherRequest.getCreated(), List.of());

        when(userRepository.existsById(1L)).thenReturn(true);
        when(requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(1L)).thenReturn(List.of(otherRequest));
        when(requestMapper.toItemRequestDtoWithItems(otherRequest)).thenReturn(otherDto);

        List<ItemRequestDto> result = requestDao.getAllRequests(1L);

        assertEquals(1, result.size());
        assertEquals("Нужен молоток", result.get(0).getDescription());
    }

    @Test
    void getAllRequests_whenUserNotFound_throwsNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> requestDao.getAllRequests(99L));
        verify(requestRepository, never()).findAllByRequestorIdNotOrderByCreatedDesc(any());
    }

    @Test
    void getRequestById_whenExists_returnsDto() {
        when(requestRepository.findById(1L)).thenReturn(Optional.of(itemRequest));
        when(requestMapper.toItemRequestDtoWithItems(itemRequest)).thenReturn(itemRequestDto);

        ItemRequestDto result = requestDao.getRequestById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Нужна дрель", result.getDescription());
    }

    @Test
    void getRequestById_whenNotFound_throwsNotFound() {
        when(requestRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> requestDao.getRequestById(99L));
    }
}
