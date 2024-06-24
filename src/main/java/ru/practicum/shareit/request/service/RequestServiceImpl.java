package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.request.dto.RequestMapper;
import ru.practicum.shareit.request.model.Request;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {

    private final RequestRepository requestRepository;
    private final UserService userService;
    private final ItemRepository itemRepository;

    public RequestDto create(Long requestorId, RequestDto requestDto) {
        checkUsrId(requestorId);

        if (requestDto.getDescription() == null || requestDto.getDescription().isBlank()) {
            log.debug("Отсутствует описание  запроса");
            throw new ValidationException("Отсутствует  описание запроса");
        }

        User requestor = userService.get(requestorId);
        Request request = RequestMapper.toRequest(requestDto, requestor);

        return RequestMapper.toRequestDto(requestRepository.save(request), new ArrayList<>());
    }

    public List<RequestDto> getAllByRequestor(Long requestorId) {

        checkUsrId(requestorId);
        userService.get(requestorId);

        List<Request> requests = requestRepository.findAllByRequestorId(requestorId);
        List<Long> requestsId = requests.stream().map(Request::getId).collect(Collectors.toList());
        List<Item> items =  itemRepository.findAllByRequestId(requestsId);
        List<RequestDto> requestsDto = new ArrayList<>();

        for(Request r : requests) {
           List<ItemDto> itemsDto = items.stream()
                   .filter(item-> item.getRequest().getId() == r.getId())
                   .map(ItemMapper::toItemDto)
                   .collect(Collectors.toList());

            requestsDto.add(RequestMapper.toRequestDto(r, itemsDto));
        }

        return requestsDto;
    }

    public List<RequestDto> getAll(Long requestorId, int from, int size) {

        checkUsrId(requestorId);
        userService.get(requestorId);

        int pageNumber = from / size;
        Pageable pageable = PageRequest.of(pageNumber, size);

        Page<Request> requestsPage = requestRepository.findAll(requestorId, pageable);
        List<Request> requests = requestsPage.getContent();
        List<Long> requestsId = requests.stream().map(Request::getId).collect(Collectors.toList());
        List<Item> items =  itemRepository.findAllByRequestId(requestsId);
        List<RequestDto> requestsDto = new ArrayList<>();

        for(Request r : requests) {
            List<ItemDto> itemsDto = items.stream()
                    .filter(item-> item.getRequest().getId() == r.getId())
                    .map(ItemMapper::toItemDto)
                    .collect(Collectors.toList());

            requestsDto.add(RequestMapper.toRequestDto(r, itemsDto));
        }

        return requestsDto;
    }

    public RequestDto get(Long requestorId, Long requestId) {
        checkUsrId(requestorId);
        userService.get(requestorId);

        Request request = requestRepository.findById(requestId)
                .orElseThrow(()-> new NotFoundException("Запрос не найден"));

        List<ItemDto> items = itemRepository.findAllByRequestId(List.of(requestId)).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());

        return RequestMapper.toRequestDto(request, items);
    }

    private void checkUsrId(Long userId) {
        if (userId == null) {
            log.debug("Отсутствует идентификатор пользователя");
            throw new ValidationException("Отсутствует идентификатор пользователя");
        }
    }
}
