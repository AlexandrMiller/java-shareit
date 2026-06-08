package ru.practicum.shareit.booking;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.jpaBooking.BookingRepository;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingsService;
import ru.practicum.shareit.exceptions.NotAvailableException;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.ValidationException;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@ExtendWith(MockitoExtension.class)
public class BookingTest {

    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;

    @InjectMocks
    private BookingsService bookingsService;

    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    private BookingShortDto shortDto;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "Booker", "booker@mail.ru");
        owner = new User(2L, "Owner", "owner@mail.ru");
        item = new Item(1L, "Дрель", "Описание", true, owner, null);
        shortDto = new BookingShortDto(1L,
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        booking = new Booking();
        booking.setId(1L);
        booking.setStart(shortDto.getStart());
        booking.setEnd(shortDto.getEnd());
        booking.setItem(item);
        booking.setBooker(booker);
        booking.setStatus(Status.WAITING);

        bookingDto = BookingMapper.toBookingDto(booking);

    }

    @Test
    void approve_whenValid_thenStatusChanged() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));
        when(userRepository.existsById(owner.getId())).thenReturn(true);

        BookingDto result = bookingsService.approve(1L, 2L, true);

        assertEquals("не ровно1", Status.APPROVED, result.getStatus());
        assertEquals("не ровно2", Status.APPROVED, booking.getStatus());
    }

    @Test
    void createBookingTest() {
        when(userRepository.findById(booker.getId())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDto dto = bookingsService.createBooking(shortDto, booker.getId());

        assertNotNull("нуль", dto);
        assertEquals("новый статус не вейтинг", Status.WAITING, dto.getStatus());
        assertEquals("ID пользователя не совпадает", booker.getId(), dto.getBooker().getId());
    }


    @Test
    void create_whenItemNotFound_thenThrowNotFoundException() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingsService.createBooking(shortDto, 1L));
    }

    @Test
    void create_whenBookerIsOwner_thenThrowNotAvailable() {
        when(userRepository.findById(2L)).thenReturn(Optional.of(owner));
        when(itemRepository.findById(1L)).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingsService.createBooking(shortDto, 2L));
    }

    @Test
    void create_whenEndBeforeStart_thenThrowValidation() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(owner));

        shortDto.setStart(LocalDateTime.now().plusDays(2));
        shortDto.setEnd(LocalDateTime.now().plusDays(1));

        assertThrows(ValidationException.class, () -> bookingsService.createBooking(shortDto, 1L));
    }

    @Test
    void approve_whenStatusNotWaiting_thenThrowValidation() {
        booking.setStatus(Status.CANCELED);

        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));
        when(userRepository.existsById(owner.getId())).thenReturn(true);

        assertThrows(ValidationException.class,
                () -> bookingsService.approve(booking.getId(),owner.getId(),true));
    }


    @Test
    void getBookersInfoById_whenNotOwnerNotBooker_thenThrowNotAvailable() {
        when(bookingRepository.findById(1L)).thenReturn(Optional.of(booking));

        assertThrows(NotAvailableException.class, () -> bookingsService.getBookingInfoById(1L, 3L));
    }
}
