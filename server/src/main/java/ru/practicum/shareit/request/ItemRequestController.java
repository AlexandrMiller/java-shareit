package ru.practicum.shareit.request;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.service.RequestDao;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@RestController
@RequestMapping(path = "/requests")
public class ItemRequestController {

    private static final String HEADER = "X-Sharer-User-Id";

    private final RequestDao requestDao;

    public ItemRequestController(RequestDao requestDao) {
        this.requestDao = requestDao;
    }

    @PostMapping
    public ItemRequest create(@RequestBody ItemRequestDto requestBody,@RequestHeader(HEADER) Long userId) {
        return requestDao.createRequest(requestBody, userId);
    }

    @GetMapping
    public List<ItemRequestDto> getMyRequests(@RequestHeader(HEADER) Long requestorId) {
        return requestDao.getItemRequestsByRequestorId(requestorId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(HEADER) Long userId) {
        return requestDao.getAllRequests(userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@PathVariable(name = "requestId") Long requestId) {
        return requestDao.getRequestById(requestId);
    }
}
