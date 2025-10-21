package ru.practicum.shareit.interfaces;

import lombok.RequiredArgsConstructor;
import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;

@Named("ItemRequestMapperUtil")
@Component
@RequiredArgsConstructor
public class ItemRequestMapperUtil {
    private final ItemRequestRepository itemRequestRepository;

    @Named("ItemRequestIdToItemRequest")
    public ItemRequest mapItemRequestIdToItemRequest(long request) {
        return itemRequestRepository.findById(request)
                .orElseThrow(() -> new EntityNotFoundException("Запрос на вещь не найден"));
    }

}
