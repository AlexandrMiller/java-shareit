package ru.practicum.shareit.booking;


import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingShortDto;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.service.BookingsService;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDAO;
import ru.practicum.shareit.request.service.RequestDao;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDao;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.time.LocalDateTime;
import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class BookingIntegTest {

    private final BookingsService bookingsService;
    private final UserDao userDao;
    private final ItemDAO itemDao;

    @Test
    void getItemsByBooker() {
        User owner = userDao.createUser(new User(null, "owner", "m@mail.ru"));
        User booker = userDao.createUser(new User(null, "booker", "k@kail.ru"));
        Item item = itemDao.postItem(owner.getId()
                , new ItemDto("item", "desc", true, null));

        BookingShortDto dto = new BookingShortDto(
                item.getId(),
                LocalDateTime.now().plusDays(1),
                LocalDateTime.now().plusDays(2));

        bookingsService.createBooking(dto, booker.getId());

        List<BookingDto> bookings = bookingsService.getBookersBookings(booker.getId(), State.ALL);

        assertThat(bookings, hasSize(1));
        assertThat(bookings.get(0).getItem().getName(), equalTo("item"));
        assertThat(bookings.get(0).getBooker().getId(), equalTo(booker.getId()));

    }
}
