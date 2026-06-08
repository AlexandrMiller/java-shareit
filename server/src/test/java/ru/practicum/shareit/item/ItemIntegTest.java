package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.service.ItemDAO;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDao;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ActiveProfiles("test")
public class ItemIntegTest {

    private final ItemDAO itemDao;
    private final UserDao userDao;

    @Test
    void getItemsByOwner_returnsOnlyOwnerItems() {
        User owner = userDao.createUser(new User(null, "owner", "owner@mail.ru"));
        User other = userDao.createUser(new User(null, "other", "other@mail.ru"));

        itemDao.postItem(owner.getId(), new ItemDto("Дрель", "Мощная дрель", true, null));
        itemDao.postItem(owner.getId(), new ItemDto("Молоток", "Тяжёлый молоток", true, null));
        itemDao.postItem(other.getId(), new ItemDto("Пила", "Острая пила", true, null));

        List<ItemLastNextDto> result = itemDao.getItemsByOwner(owner.getId());

        assertThat(result, hasSize(2));
        assertThat(result, everyItem(hasProperty("lastBooking", nullValue())));
        assertThat(result, everyItem(hasProperty("nextBooking", nullValue())));
        assertThat(result.stream().map(ItemLastNextDto::getName).toList(),
                containsInAnyOrder("Дрель", "Молоток"));
    }

    @Test
    void getItemsByOwner_emptyWhenOwnerHasNoItems() {
        User owner = userDao.createUser(new User(null, "lonely", "lonely@mail.ru"));

        List<ItemLastNextDto> result = itemDao.getItemsByOwner(owner.getId());

        assertThat(result, empty());
    }

    @Test
    void getItemsByOwner_throwsWhenUserNotFound() {
        assertThrows(NotFoundException.class, () -> itemDao.getItemsByOwner(999L));
    }
}
