package ru.practicum.shareit.item;

import jakarta.websocket.server.PathParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemDAO;

import java.nio.file.AccessDeniedException;
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
    public Item getItemById(@PathVariable Long itemId) {
        return itemDao.getItemById(itemId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwner(@RequestHeader("X-Sharer-User-Id") Long id) {
        return itemDao.getItemsByOwner(id);
    }

    @GetMapping("/search")
    public Collection<ItemDto> searchItem(@RequestParam String text) {
        return itemDao.searchItem(text);
    }


}
