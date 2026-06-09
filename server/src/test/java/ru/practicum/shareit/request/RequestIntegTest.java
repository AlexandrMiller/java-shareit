package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemDAO;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestDao;
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
public class RequestIntegTest {

    private final RequestDao requestDao;
    private final UserDao userDao;
    private final ItemDAO itemDao;

    @Test
    void createRequest_andGetById_returnsCorrectRequest() {
        User user = userDao.createUser(new User(null, "Иван", "ivan@mail.ru"));

        ItemRequest created = requestDao.createRequest(
                new ItemRequestDto(null, "Нужна дрель", null, null), user.getId());

        ItemRequestDto found = requestDao.getRequestById(created.getId());

        assertThat(found.getId(), equalTo(created.getId()));
        assertThat(found.getDescription(), equalTo("Нужна дрель"));
        assertThat(found.getCreated(), notNullValue());
    }

    @Test
    void createRequest_whenUserNotFound_throwsNotFound() {
        assertThrows(NotFoundException.class,
                () -> requestDao.createRequest(new ItemRequestDto(null, "текст", null, null), 999L));
    }

    @Test
    void getItemRequestsByRequestorId_returnsOnlyOwnRequests() {
        User user = userDao.createUser(new User(null, "Пользователь", "user@mail.ru"));
        User other = userDao.createUser(new User(null, "Другой", "other@mail.ru"));

        requestDao.createRequest(new ItemRequestDto(null, "Запрос 1", null, null), user.getId());
        requestDao.createRequest(new ItemRequestDto(null, "Запрос 2", null, null), user.getId());
        requestDao.createRequest(new ItemRequestDto(null, "Чужой запрос", null, null), other.getId());

        List<ItemRequestDto> result = requestDao.getItemRequestsByRequestorId(user.getId());

        assertThat(result, hasSize(2));
        assertThat(result.stream().map(ItemRequestDto::getDescription).toList(),
                containsInAnyOrder("Запрос 1", "Запрос 2"));
    }

    @Test
    void getItemRequestsByRequestorId_whenUserNotFound_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> requestDao.getItemRequestsByRequestorId(999L));
    }

    @Test
    void getAllRequests_returnsOnlyOtherUsersRequests() {
        User user = userDao.createUser(new User(null, "Я", "me@mail.ru"));
        User other = userDao.createUser(new User(null, "Другой", "another@mail.ru"));

        requestDao.createRequest(new ItemRequestDto(null, "Мой запрос", null, null), user.getId());
        requestDao.createRequest(new ItemRequestDto(null, "Чужой запрос", null, null), other.getId());

        List<ItemRequestDto> result = requestDao.getAllRequests(user.getId());

        assertThat(result, hasSize(1));
        assertThat(result.get(0).getDescription(), equalTo("Чужой запрос"));
    }

    @Test
    void getRequestById_includesItemsLinkedToRequest() {
        User requestor = userDao.createUser(new User(null, "Запросчик", "req@mail.ru"));
        User owner = userDao.createUser(new User(null, "Владелец", "own@mail.ru"));

        ItemRequest request = requestDao.createRequest(
                new ItemRequestDto(null, "Нужна пила", null, null), requestor.getId());

        itemDao.postItem(owner.getId(),
                new ItemDto("Пила", "Острая пила", true, request.getId()));

        ItemRequestDto result = requestDao.getRequestById(request.getId());

        assertThat(result.getItems(), hasSize(1));
        assertThat(result.getItems().get(0).getName(), equalTo("Пила"));
    }

    @Test
    void getRequestById_whenNotFound_throwsNotFound() {
        assertThrows(NotFoundException.class, () -> requestDao.getRequestById(999L));
    }
}
