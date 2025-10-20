package ru.practicum.events;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.HitDto;
import ru.practicum.categories.Category;
import ru.practicum.categories.CategoryMapper;
import ru.practicum.categories.CategoryRepository;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.requests.*;
import ru.practicum.requests.dto.ParticipationRequestDto;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.stats.StatsClient;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.time.format.DateTimeFormatter.ofPattern;

@Service
@RequiredArgsConstructor
@Transactional
public class EventServiceImpl implements EventService {
    private final EventsRepository eventsRepository;
    private final EventMapper eventMapper;
    private final CategoryMapper categoryMapper;
    private final RequestMapper requestMapper;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statsClient;

    @Override
    public List<EventFullDto> getAllFilteredEvents(List<Long> users, List<String> states, List<Long> categories, LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventsRepository.getAllFilteredEvents(users, states, categories, rangeStart, rangeEnd, pageable);

        if (!events.isEmpty()) {
            return eventMapper.toEventFullDtos(events);
        }

        return List.of();
    }

    @Override
    public EventFullDto updateFilteredEvent(Long eventId, UpdateEventAdminRequest eventUpdateDto) {
        if (eventsRepository.findById(eventId).isEmpty()) {
            throw new CommonNotFoundException("Event for id " + eventId + " was not found");
        }

        Event event = eventsRepository.findById(eventId).get();

        if (eventUpdateDto.getEventDate() != null) {
            LocalDateTime newEventDate = eventUpdateDto.getEventDate();
            if (event.getPublishedOn() != null && newEventDate.isBefore(event.getPublishedOn().plusHours(1))) {
                throw new CommonConflictException("Event date must be at least one hour after publication date");
            }
            event.setEventDate(newEventDate);
        }

        if (eventUpdateDto.getStateAction() != null) {
            StateAction newActionState = eventUpdateDto.getStateAction();
            if (newActionState.equals(StateAction.REJECT_EVENT)) {
                if (event.getPublishedOn() != null) {
                    throw new CommonConflictException("Event cannot be canceled. It is already published");
                } else {
                    event.setState(Status.CANCELED);
                }
            } else {
                if (event.getPublishedOn() != null || event.getState().equals(Status.CANCELED)) {
                    throw new CommonConflictException("Event is already published or canceled");
                }
                event.setPublishedOn(LocalDateTime.now());
                event.setState(Status.PUBLISHED);
            }

        }

        if (eventUpdateDto.getAnnotation() != null) {
            event.setAnnotation(eventUpdateDto.getAnnotation());
        }
        if (eventUpdateDto.getCategory() != null) {
            event.setCategory(categoryMapper.toCategoryFromDto(eventUpdateDto.getCategory()));
        }
        if (eventUpdateDto.getDescription() != null) {
            event.setDescription(eventUpdateDto.getDescription());
        }
        if (eventUpdateDto.getLocation() != null) {
            event.setLocation(eventUpdateDto.getLocation());
        }
        if (eventUpdateDto.getPaid() != null) {
            event.setPaid(eventUpdateDto.getPaid());
        }
        if (eventUpdateDto.getParticipantLimit() != null) {
            event.setParticipantLimit(eventUpdateDto.getParticipantLimit());
        }
        if (eventUpdateDto.getRequestModeration() != null) {
            event.setRequestModeration(eventUpdateDto.getRequestModeration());
        }
        if (eventUpdateDto.getTitle() != null) {
            event.setTitle(eventUpdateDto.getTitle());
        }

        Event eventSaved = eventsRepository.save(event);
        return eventMapper.toEventFullDto(eventSaved);
    }

    @Override
    public List<EventShortDto> getAllPublicFilteredEvents(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart, LocalDateTime rangeEnd, Boolean onlyAvailable, EventSort sort, Integer from, Integer size, HttpServletRequest request) {
        Boolean rangeQuery = false;

        if ((rangeStart != null && rangeEnd != null) && rangeStart.isBefore(rangeEnd)) {
            rangeQuery = true;
        }

        String sortField = EventSort.EVENT_DATE.equals(sort) ? "eventDate" : "views";
        Sort.Direction sortDirection = Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(from / size, size, Sort.by(sortDirection, sortField));

        List<Event> events = new ArrayList<>();

        if (rangeQuery) {
            events = eventsRepository.findPublicFilteredEvents(text, categories, paid, rangeStart, rangeEnd, onlyAvailable);
        } else {
            LocalDateTime date = LocalDateTime.now();
            events = eventsRepository.findPublicFilteredEventsAfterNow(text, categories, paid, date, onlyAvailable, pageable);
        }

        if (events.isEmpty()) {
            return List.of();
        }

        addSeveralHits(request.getRemoteAddr(), events, LocalDateTime.now());
        return eventMapper.toEventShortDtos(events);
    }

    @Override
    public EventFullDto getPublicFilteredEventById(Long eventId, HttpServletRequest request) {
        if (eventsRepository.findById(eventId).isEmpty()) {
            throw new CommonNotFoundException("Event with id " + eventId + " was not found.");
        }

        Event event = eventsRepository.findById(eventId).get();

        if (!event.getState().equals(Status.PUBLISHED)) {
            throw new CommonNotFoundException("Event with id " + eventId + " was not found.");
        }

        addOneHit(request.getRemoteAddr(), event.getId(), LocalDateTime.now());
        return eventMapper.toEventFullDto(event);
    }

    @Override
    public List<EventShortDto> getAllUserEvents(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Event> events = eventsRepository.findAllByInitiatorId(userId, pageable);

        return events.isEmpty() ? null : eventMapper.toEventShortDtos(events);

    }

    @Override
    public EventFullDto getEventByUserAndId(Long userId, Long eventId) {
        Event event = eventsRepository.findByIdAndInitiatorId(userId, eventId).orElseThrow(() -> new CommonNotFoundException(
                "Event with id " + eventId + " and initiator id " + userId + " was not found"));

        return eventMapper.toEventFullDto(event);
    }

    @Override
    public EventFullDto createEvent(Long userId, NewEventDto newEventDto) {
        if (newEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new CommonBadRequestException("Event should be at least 2 hours earlier then actual timestamp");
        }

        Long categoryId = newEventDto.getCategory();
        if (categoryRepository.findById(categoryId).isEmpty()) {
            throw new CommonNotFoundException("Category with id " + categoryId + " for the event does not exist");
        }
        Category category = categoryRepository.findById(categoryId).get();
        Event event = eventMapper.toEventFromNewDto(newEventDto);
        event.setCategory(category);
        User user = userRepository.findById(userId).orElseThrow(() -> new
                CommonNotFoundException("User with id " + userId + " does not exist"));
        event.setInitiator(user);
        Event savedEvent = eventsRepository.save(event); // Persist the event
        return eventMapper.toEventFullDto(savedEvent);
    }

    @Override
    public ParticipationRequestDto getParticipantRequestByUserAndEventIds(Long userId, Long eventId) {
        Request request = requestRepository.getRequestByEventAndRequester(userId, eventId).orElseThrow(() ->
                new CommonNotFoundException("Request for event " + eventId +
                        " with userId " + userId + " was not found"));
        return requestMapper.toRequestDto(request);
    }

    @Override
    public EventFullDto updateEventByUserAndEventIds(Long userId, Long eventId, UpdateEventUserRequest updateEventDto) {
        if (eventsRepository.findByIdAndInitiatorId(eventId, userId).isEmpty()) {
            throw new CommonNotFoundException("Event with" + eventId + " was not found");
        }

        Event event = eventsRepository.findByIdAndInitiatorId(eventId, userId).get();

        if (event.getPublishedOn() != null) {
            throw new CommonBadRequestException("Event cant be changed. Event is already published");
        }

        if (updateEventDto == null) {
            return eventMapper.toEventFullDto(event);
        }

        if (updateEventDto.getEventDate() != null && updateEventDto.getEventDate().isBefore(LocalDateTime.now().plusHours(2))) {
            throw new CommonBadRequestException("Event should be at least 2 hours earlier then actual timestamp");
        }
        event.setEventDate(updateEventDto.getEventDate());

        if (updateEventDto.getAnnotation() != null) {
            event.setAnnotation(updateEventDto.getAnnotation());
        }

        if (updateEventDto.getDescription() != null) {
            event.setDescription(updateEventDto.getDescription());
        }

        if (updateEventDto.getLocation() != null) {
            event.setLocation(updateEventDto.getLocation());
        }

        if (updateEventDto.getPaid() != null) {
            event.setPaid(updateEventDto.getPaid());
        }

        if (updateEventDto.getParticipantLimit() != null) {
            event.setParticipantLimit(updateEventDto.getParticipantLimit());
        }

        if (updateEventDto.getRequestModeration() != null) {
            event.setRequestModeration(updateEventDto.getRequestModeration());
        }

        if (updateEventDto.getTitle() != null) {
            event.setTitle(updateEventDto.getTitle());
        }

        if (updateEventDto.getCategory() != null) {
            Category category = categoryRepository.findById(updateEventDto.getCategory()).orElseThrow(
                    () -> new CommonNotFoundException("Category was not found"));
            event.setCategory(category);
        }

        StateActionReview stateActionReview = updateEventDto.getStateAction();
        if (updateEventDto.getStateAction() != null) {
            if (stateActionReview.equals(StateActionReview.CANCEL_REVIEW)) {
                event.setState(Status.CANCELED);
            } else {
                event.setState(Status.PENDING);
            }
        }
        Event eventToSave = eventsRepository.save(event);
        return eventMapper.toEventFullDto(eventToSave);
    }

    @Override
    public EventRequestStatusUpdateResult updateEventStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest eventRequestStatusUpdateRequest) {
        Event event = eventsRepository.findById(eventId)
                .orElseThrow(() -> new CommonNotFoundException("Event with id " + eventId + " was not found"));
        if (!userId.equals(event.getInitiator().getId())) {
            throw new CommonBadRequestException("User with id " + userId + " is not the initiator of event with id " + eventId);
        }

        // Return empty result if no moderation or no participant limit
        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
            result.setConfirmedRequests(new ArrayList<>());
            result.setRejectedRequests(new ArrayList<>());
            return result;
        }

        // Fetch requests for the event and initiator
        List<Request> requests = requestRepository.findAllByEventAndInitiator(userId, eventId);

        // Filter requests by provided requestIds
        List<Request> requestsToUpdate = requests.stream()
                .filter(request -> eventRequestStatusUpdateRequest.getRequestIds().contains(request.getId()))
                .collect(Collectors.toList());
        if (requestsToUpdate.isEmpty()) {
            throw new CommonNotFoundException("No matching requests found for the provided request IDs");
        }

        // Validate status changes
        if (requestsToUpdate.stream()
                .anyMatch(request -> RequestStatus.CONFIRMED.equals(request.getStatus()) &&
                        RequestStatus.REJECTED.equals(eventRequestStatusUpdateRequest.getStatus()))) {
            throw new CommonBadRequestException("Cannot reject already confirmed requests");
        }
        if (RequestStatus.CONFIRMED.equals(eventRequestStatusUpdateRequest.getStatus()) &&
                event.getConfirmedRequests() + requestsToUpdate.size() > event.getParticipantLimit()) {
            throw new CommonConflictException("Participant limit reached for event with id " + eventId);
        }

        // Update request statuses
        for (Request request : requestsToUpdate) {
            request.setStatus(eventRequestStatusUpdateRequest.getStatus());
        }
        requestRepository.saveAll(requestsToUpdate);

        // Update confirmed requests count if status is CONFIRMED
        if (RequestStatus.CONFIRMED.equals(eventRequestStatusUpdateRequest.getStatus())) {
            event.setConfirmedRequests(event.getConfirmedRequests() + requestsToUpdate.size());
            eventsRepository.save(event);
        }

        // Build result
        EventRequestStatusUpdateResult result = new EventRequestStatusUpdateResult();
        result.setConfirmedRequests(new ArrayList<>());
        result.setRejectedRequests(new ArrayList<>());
        List<ParticipationRequestDto> updatedDtos = requestMapper.toRequestDtos(requestsToUpdate);
        if (RequestStatus.CONFIRMED.equals(eventRequestStatusUpdateRequest.getStatus())) {
            result.setConfirmedRequests(updatedDtos);
        } else if (RequestStatus.REJECTED.equals(eventRequestStatusUpdateRequest.getStatus())) {
            result.setRejectedRequests(updatedDtos);
        }

        return result;
    }
/*
    private void addHit(Event event,
                        HttpServletRequest request) {
        LocalDateTime hitTime = LocalDateTime.now();
        HitDto hitDto = HitDto.builder()
                .ip(request.getRemoteAddr())
                .app("main")
                .uri("/events")
                .timestamp(hitTime.format(ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        statsClient.addHit(hitDto);
        addOneHit(
                request.getRemoteAddr(),
                event.getId(),
                hitTime
        );
    }*/

    private void addOneHit(String ip, Long eventId,
                           LocalDateTime hitTime) {
        HitDto hitDto = HitDto.builder()
                .ip(ip)
                .app("main")
                .uri("/events/" + eventId)
                .timestamp(hitTime.format(ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();

        statsClient.addHit(hitDto);
    }

    private void addSeveralHits(String ip,
                                List<Event> events,
                                LocalDateTime hitTime) {
        for (Event event : events) {
            HitDto hitDto = HitDto.builder()
                    .ip(ip)
                    .app("main")
                    .uri("/events/" + event.getId())
                    .timestamp(hitTime.format(ofPattern("yyyy-MM-dd HH:mm:ss")))
                    .build();

            statsClient.addHit(hitDto);
            ;
        }
    }

}
