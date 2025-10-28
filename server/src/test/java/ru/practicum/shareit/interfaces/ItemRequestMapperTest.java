package ru.practicum.shareit.interfaces;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exceptions.EntityNotFoundException;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


@Transactional
@ExtendWith(SpringExtension.class)
@SpringBootTest
class ItemRequestMapperTest {

    @Autowired
    private UserService userService;
    @Autowired
    private ItemRequestService itemRequestService;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    @Autowired
    private ItemRequestMapper itemRequestMapper;

    private Long requestorId;
    private List<ItemRequest> itemRequests;

    @BeforeEach
    void setUp() {
        // Данные для тестирования
        itemRequests = new ArrayList<>();
        // Создаем пользователя
        UserResponseDto requestorDto = createUser("Алеша Попович", "Popovich@mail.com");
        requestorId = requestorDto.getId();
        // Создаем запросы
        ItemRequestDto clubRequestDto = createRequestDto("Чтоб глушить", requestorId);
        ItemRequestDto maceRequestDto = createRequestDto("Чтоб крушить", requestorId);
        ItemRequestDto swordRequestDto = createRequestDto("Чтоб рубить", requestorId);

        ItemRequest clubRequest = getRequest(clubRequestDto.getId());
        ItemRequest maceRequest = getRequest(maceRequestDto.getId());
        ItemRequest swordRequest = getRequest(swordRequestDto.getId());

        itemRequests = List.of(clubRequest, maceRequest, swordRequest);
    }

    private UserResponseDto createUser(String name, String email) {
        return userService.postUser(new UserRequestDto(name, email));
    }

    private ItemRequestDto createRequestDto(String description, Long requestorId) {
        return itemRequestService.postItemRequest(requestorId, new ItemRequestDto(description, requestorId));
    }

    private ItemRequest getRequest(Long requestId) {
        return itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new EntityNotFoundException("Запрос не найден"));
    }

    @Test
    void toListDtoIfItemRequestIsNull() {
        List<ItemRequestDto> result = itemRequestMapper.toListDto(null);

        assertThat(result).isNull();
    }

    @Test
    void toListDto() {
        List<ItemRequestDto> result = itemRequestMapper.toListDto(itemRequests);

        assertThat(result).isNotNull();
        assertThat(result).hasSize(itemRequests.size());
        assertThat(result.getFirst()).hasFieldOrProperty("description");
        assertThat(result.getFirst().getRequestor()).isEqualTo(requestorId);
    }

}