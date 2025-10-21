package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.CommentMapper;
import ru.practicum.shareit.interfaces.ItemMapper;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemMapper itemMapper;
    private final CommentMapper commentMapper;

    @Transactional
    @Override
    public ItemResponseDto postItem(ItemRequestDto itemRequestDto, Long ownerId) {
        log.debug("postItem. Добавление вещи {} c ownerId {}", itemRequestDto, ownerId);
        itemRequestDto.setOwner(ownerId);

        userService.getById(ownerId);

        Item item = itemMapper.toEntity(itemRequestDto);

        return itemMapper.toDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemResponseDto patchItem(Long itemId, Long userId, ItemRequestDto itemRequestDto) {

        Item oldItem = getById(itemId);
        if (!oldItem.getOwner().getId().equals(userId)) {
            throw new EntityNotFoundException("Вещь не принадлежит данному пользователю");
        }

        Item newItem = itemMapper.updateField(itemRequestDto, oldItem);
        newItem.setOwner(userService.getById(userId));

        return itemMapper.toDto(itemRepository.save(newItem));
    }

    @Override
    public ItemResponseWithBookingDateDto getItemById(Long itemId, Long userId) {

        userService.getById(userId);
        Item item = getById(itemId);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList();

        return itemMapper.toWithBookingDateAndCommentsDto(item,
                bookingService.findLastBooking(item.getId(),
                        userId,
                        Status.APPROVED,
                        LocalDateTime.now()),
                bookingService.findNextBooking(item.getId(),
                        userId,
                        Status.APPROVED, LocalDateTime.now()),
                comments);
    }

    @Override
    public List<ItemResponseDto> getItemByText(String text) {
        log.debug("getItemByText. Поиск в имени и описании по части строки {}", text);
        if (text.isEmpty()) {
            return List.of();
        }
        Optional<Collection<Item>> items = itemRepository.findAllByNameOrDescriptionContaining(text);
        return items
                .orElseThrow(() -> new EntityNotFoundException("Вещь не найдена"))
                .stream()
                .map(itemMapper::toDto)
                .toList();
    }

    @Override
    public List<ItemResponseWithBookingDateDto> getItems(Long userId) {

        userService.getById(userId);

        Collection<Item> items = itemRepository.findAllByUserId(userId)
                .orElseThrow(() -> new EntityNotFoundException("Список вещей не найден"));

        Map<Long, List<CommentDto>> comments = new HashMap<>();
        items.stream()
                .map(item -> {
                    return comments.put(item.getId(),
                            commentMapper.toListCommentDto(commentRepository.findAllByItemId(item.getId())));
                })
                .toList();

        return items.stream()
                .map(item -> itemMapper.toWithBookingDateAndCommentsDto(item,
                        bookingService.findLastBooking(item.getId(),
                                userId,
                                Status.APPROVED,
                                LocalDateTime.now()),
                        bookingService.findNextBooking(item.getId(),
                                userId,
                                Status.APPROVED,
                                LocalDateTime.now()),
                        comments.get(item.getId())))
                .toList();
    }

    @Transactional
    @Override
    public CommentDto postComment(CommentDto commentDto, Long itemId, Long userId, LocalDateTime timeNow) {

        User author = userService.getById(userId);
        Item item = getById(itemId);

        bookingService.checkingThatTheUserHasRentedTheItem(itemId,
                userId,
                Status.APPROVED,
                timeNow);
        Comment comment = commentRepository.save(commentMapper.toEntityComment(commentDto, item, author));
        return commentMapper.toCommentDto(comment);
    }

    public List<ItemResponseWithOwnerIdDto> findItemByRequestIdWithOwnerId(Long requestId) {
        return itemMapper.toWithOwnerIdDto(itemRepository.findAllByRequestId(requestId));
    }

    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вещь Item не найдена"));
    }
}