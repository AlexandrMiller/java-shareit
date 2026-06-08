package ru.practicum.shareit.request.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.request.mapper.RequestMapper;
import ru.practicum.shareit.request.RequestRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.jpaUser.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class RequestDao {

    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    public RequestDao(RequestRepository requestRepository,
                      UserRepository userRepository,
                      RequestMapper requestMapper) {
        this.requestRepository = requestRepository;
        this.userRepository = userRepository;
        this.requestMapper = requestMapper;
    }

    @Transactional
    public ItemRequest createRequest(ItemRequestDto requestBody, Long userId) {

        User user = userRepository
                .findById(userId)
                .orElseThrow(
                        () -> new NotFoundException("Только зарегистрированный пользователь может создать запрос"));

        ItemRequest itemRequest = new ItemRequest(null, requestBody.getDescription(), LocalDateTime.now(), user);

        return requestRepository.save(itemRequest);
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getItemRequestsByRequestorId(Long requestorId) {

        if (!userRepository.existsById(requestorId)) {
            throw new NotFoundException("Пользователь не найден");
        }

        List<ItemRequest> requests = requestRepository.findByRequestorIdOrderByCreatedDesc(requestorId);

        return requests
                .stream()
                .map(requestMapper::toItemRequestDtoWithItems)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId) {


        if (!userRepository.existsById(userId)) {
            throw new NotFoundException("Пользователь с id " + userId + " не найден");
        }

        List<ItemRequest> items = requestRepository.findAllByRequestorIdNotOrderByCreatedDesc(userId);

        return items.stream()
                .map(requestMapper::toItemRequestDtoWithItems)
                .toList();
    }

    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long requestId) {

        ItemRequest itemRequest = requestRepository
                .findById(requestId).orElseThrow(() -> new NotFoundException("Запрос не найден"));

        return requestMapper.toItemRequestDtoWithItems(itemRequest);
    }
}
