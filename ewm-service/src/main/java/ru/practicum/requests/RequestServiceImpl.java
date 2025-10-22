package ru.practicum.requests;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.users.UserRepository;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final UserRepository userRepository;
    private final EventsRepository eventsRepository;
    private final RequestMapper requestMapper;

    @Override
    public List<ParticipationRequestDto> getRequestsByUserId(Long userId) {
        userRepository.findById(userId).orElseThrow(
                () -> new CommonNotFoundException("User with " + userId + " was not found."));
        List<Request> requests = requestRepository.findAllByRequester(userId);
        return requests.isEmpty() ? List.of() : requestMapper.toRequestDtos(requests);
    }

    @Override
    public ParticipationRequestDto postNewRequest(Long userId, Long eventId) {
        if (requestRepository.getRequestByEventAndRequester(eventId, userId).isPresent()) {
            throw new CommonConflictException("Request with userId " + userId +
                    " and eventId " + eventId + " already exists");
        }

        Optional<Event> event = eventsRepository.findById(eventId);
        if (event.isEmpty()) {
            throw new CommonNotFoundException("Requested event with id " + eventId + " was not found");
        }

        if (event.get().getPublishedOn() == null) {
            throw new CommonConflictException("Participation request is possible only in a published event");
        }

        if (event.get().getInitiator().getId().equals(userId)) {
            throw new CommonConflictException("Event initiator can not be a requester");
        }

        if (event.get().getParticipantLimit() != null &&
                event.get().getParticipantLimit().equals(event.get().getConfirmedRequests())) {
            throw new CommonConflictException("Event participation limit reached");
        }

        List<Request> requests = requestRepository.findAllByEvent(eventId);

        if (!event.get().getRequestModeration() && requests.size() >= event.get().getParticipantLimit())
            throw new CommonConflictException("Event participation limit reached");

        Request newRequest = new Request();
        newRequest.setRequester(userId);
        newRequest.setEvent(eventId);
        newRequest.setCreated(LocalDateTime.now().truncatedTo(ChronoUnit.SECONDS));

        newRequest.setStatus(event.get().getRequestModeration() ? RequestStatus.PENDING : RequestStatus.CONFIRMED);

        Request savedRequest = requestRepository.save(newRequest);
        return requestMapper.toRequestDto(savedRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequestsByUserIdAndEventId(Long userId, Long requestId) {
        Request request = requestRepository.findByIdAndRequester(requestId, userId).orElseThrow(() ->
                new CommonNotFoundException("While request canceling. Request with id " + requestId + " for user with id " + userId + " was not found"));
        request.setStatus(RequestStatus.CANCELED);
        Request savedRequest = requestRepository.save(request);
        return requestMapper.toRequestDto(savedRequest);
    }
}
