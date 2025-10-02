package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingServiceImpl;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.interfaces.CommentMapper;
import ru.practicum.shareit.interfaces.ItemMapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.dto.ItemRequestDto;
import ru.practicum.shareit.item.dto.ItemResponseWithBookingDateDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.model.User;

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
    public ItemResponseDto postItem(ItemRequestDto itemRequestDto) {
        log.debug("postItem. Добавление вещи {} c ownerId {}", itemRequestDto, itemRequestDto.getOwner());

        userService.getById(itemRequestDto.getOwner());

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

        Item item = getById(itemId);

        List<CommentDto> comments = commentRepository.findAllByItemId(itemId).stream()
                .map(commentMapper::toCommentDto)
                .toList();

        return itemMapper.toWithBookingDateAndCommentsDto(item,
                bookingService.findLastBooking(item.getId(),
                        userId,
                        Status.APPROVED),
                bookingService.findNextBooking(item.getId(),
                        userId,
                        Status.APPROVED),
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
    public Collection<ItemResponseWithBookingDateDto> getItems(Long userId) {

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
                                Status.APPROVED),
                        bookingService.findNextBooking(item.getId(),
                                userId,
                                Status.APPROVED),
                        comments.get(item.getId())))
                .toList();
    }

    @Transactional
    @Override
    public CommentDto postComment(CommentDto commentDto, Long itemId, Long userId) {

        User author = userService.getById(userId);
        Item item = getById(itemId);

        bookingService.checkingThatTheUserHasRentedTheItem(itemId,
                userId,
                Status.APPROVED);
        Comment comment = commentRepository.save(commentMapper.toEntityComment(commentDto, item, author));
        return commentMapper.toCommentDto(comment);
    }

    public Item getById(Long id) {
        return itemRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Вещь Item не найдена"));
    }
}