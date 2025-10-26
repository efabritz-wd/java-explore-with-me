package ru.practicum.requests;

import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    List<ParticipationRequestDto> getRequestsByUserId(Long userId);

    ParticipationRequestDto postNewRequest(Long userId, Long eventId);

    ParticipationRequestDto cancelRequestsByUserIdAndEventId(Long userId, Long requestId);
}
