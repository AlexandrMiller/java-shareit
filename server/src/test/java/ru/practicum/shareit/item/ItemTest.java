package ru.practicum.shareit.item;

import jakarta.validation.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.jpaBooking.BookingRepository;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.service.BookingsService;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.jpaItem.CommentRepository;
import ru.practicum.shareit.item.jpaItem.ItemRepository;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDAO;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserDao;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.*;

@ExtendWith(MockitoExtension.class)
public class ItemTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ItemRepository itemRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private BookingsService bookingsService; //используется в методе getItemById().

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private UserDao userDao;

    @InjectMocks
    private ItemDAO itemDAO;

    private User owner;
    private Item item;
    private ItemDto itemDto;
    private Comment comment;
    private List<Comment> comments;
    private CommentDto commentDto;
    private User commentator;

    @BeforeEach
    void setUp() {
        owner = new User(1L, "Owner", "owner@mail.ru");
        item = new Item(1L, "Дрель", "Описание", true, owner, null);
        itemDto = ItemMapper.toItemDto(item);
        comment = new Comment(1L, "commentText", item, owner, LocalDateTime.now());
        comments = List.of(comment);
        commentDto = CommentMapper.toCommentDto(comment);
        commentator = new User(10L, "commentator", "comm@m.ru");
    }

    @Test
    void postItem() {

        when(userDao.getUserById(1L)).thenReturn(owner);
        when(itemRepository.save(any(Item.class))).thenReturn(item);



        Item result = itemDAO.postItem(1L, itemDto);

        assertEquals("имя не ровно", item.getName(), result.getName());
        assertEquals("описание не ровно", item.getDescription(), result.getDescription());
        assertEquals("статус доступности не равен", item.getAvailable(), result.getAvailable());

        verify(userDao).getUserById(1L);
        verify(itemRepository).save(any(Item.class));
    }

    @Test
    void postItem_whenUserNotFound() {
        Long nonExistentUserId = 999L;

        when(userDao.getUserById(nonExistentUserId))
                .thenThrow(new NotFoundException("Пользователь не найден"));

        assertThrows(NotFoundException.class,
                () -> itemDAO.postItem(nonExistentUserId, itemDto));

    }


    @Test
    void updateItemTest() {

        Long itemId = 1L;
        Long ownerId = 1L;

        Item existingItem = new Item(itemId, "oldname", "olddesc", false, owner, null);
        ItemDto updateDto = new ItemDto();
        updateDto.setName("newname");
        updateDto.setDescription("newdesc");
        updateDto.setAvailable(true);

        Item updatedItem = new Item(itemId, "newname", "newdesc", true, owner, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(existingItem));
        when(itemRepository.save(any(Item.class))).thenReturn(updatedItem);

        Item result = itemDAO.updateItem(updateDto, itemId, ownerId);

        assertEquals("имя не ровно", "newname", result.getName());
        assertEquals("описание не ровно", "newdesc", result.getDescription());
        assertTrue(result.getAvailable());
    }


    @Test
    void updateItem_whenNotOwner_thenThrowNotFound() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));

        assertThrows(NotFoundException.class, () -> itemDAO.updateItem(itemDto, 1L, 999L));
    }


    @Test
    void updateItem_whenItemNotExist_thenThrowNotFound() {
         Long nonExistId = 100L;
         when(itemRepository.findById(nonExistId)).thenReturn(Optional.empty());

         assertThrows(NotFoundException.class,() -> itemDAO.updateItem(itemDto, nonExistId, 1L));

         verify(itemRepository, never()).save(any());
    }


    @Test
    void getItemByIdTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(commentRepository.findByItemIdOrderByCreatedDesc(item.getId())).thenReturn(comments);
        ItemLastNextDto result = itemDAO.getItemById(item.getId(), owner.getId());

        assertEquals("ids not equal", item.getId(), result.getId());
        assertEquals("имена не равны", item.getName(), result.getName());
        assertNotNull("комменты пусты", result.getComments());
        assertEquals("описания не равны", item.getDescription(), result.getDescription());
    }

    @Test
    void searchTest() {
        when(itemRepository.searchItems(anyString())).thenReturn(List.of(item));

        Collection<ItemDto> result = itemDAO.searchItem("дрель");

        assertEquals("размер списка должен быть 1", 1, result.size());
        verify(itemRepository).searchItems("дрель");
    }

    @Test
    void createCommentTest() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(10L)).thenReturn(Optional.of(commentator));
        when(bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(),
                        anyLong(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(true);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);

        CommentDto result = itemDAO.createComment(commentDto, 1L, 10L);



        assertEquals("ss", comment.getAuthor().getName(), result.getAuthorName());
        assertEquals("ss", "commentText", result.getText());
        verify(commentRepository).save(any());
    }

    @Test
    void createComment_whenCantComment_thenThrowValidation() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(10L)).thenReturn(Optional.of(commentator));
        when(bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(),
                        anyLong(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(false);

        assertThrows(ValidationException.class, () -> itemDAO.createComment(commentDto,1L, 10L));
    }

    @Test
    void createComment_whenTextIsNull_thenThrowValidation() {
        when(itemRepository.findById(item.getId())).thenReturn(Optional.of(item));
        when(userRepository.findById(10L)).thenReturn(Optional.of(commentator));
        when(bookingRepository
                .existsByBookerIdAndItemIdAndStatusAndEndBefore(anyLong(),
                        anyLong(),
                        any(Status.class),
                        any(LocalDateTime.class)))
                .thenReturn(true);
        CommentDto nullText = new CommentDto(null, null, commentator.getName(), LocalDateTime.now());

        assertThrows(ValidationException.class, () -> itemDAO.createComment(nullText, 1L, 10L));

    }




}
