package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemLastNextDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDAO;

import java.util.Collection;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequestMapping("/items")
public class ItemController {


    private final ItemDAO itemDao;

    public ItemController(ItemDAO itemDao) {
        this.itemDao = itemDao;
    }

    @PostMapping
    public Item postItem(@RequestBody ItemDto dto, @RequestHeader("X-Sharer-User-Id") long userId) {

      return itemDao.postItem(userId,dto);
    }

    @PatchMapping("/{itemId}")
    public Item updateItem(@RequestBody ItemDto dto,
                           @RequestHeader("X-Sharer-User-Id") Long ownerId,
                           @PathVariable Long itemId) {
        return itemDao.updateItem(dto, itemId, ownerId);
    }

    @GetMapping("/{itemId}")
    public ItemLastNextDto getItemById(@PathVariable Long itemId, @RequestHeader("X-Sharer-User-Id") Long id) {
        return itemDao.getItemById(itemId,id);
    }

    @GetMapping
    public List<ItemLastNextDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemDao.getItemsByOwner(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        return itemDao.searchItem(text);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto createComment(@RequestBody CommentDto commentDto,
                                    @RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long itemId) {
        return itemDao.createComment(commentDto,itemId,userId);
    }


}
