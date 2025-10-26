package ru.practicum.requests;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/users/{userId}/requests")
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<ParticipationRequestDto> getRequestsByUserId(@PathVariable("userId") Long userId) {
        return requestService.getRequestsByUserId(userId);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto postRequestsByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                                 @RequestParam Long eventId) {
        return requestService.postNewRequest(userId, eventId);
    }

    @PatchMapping("/{requestId}/cancel")
    public ParticipationRequestDto updateRequestsByUserIdAndEventId(@PathVariable("userId") Long userId,
                                                                    @PathVariable("requestId") Long requestId) {
        return requestService.cancelRequestsByUserIdAndEventId(userId, requestId);
    }

}