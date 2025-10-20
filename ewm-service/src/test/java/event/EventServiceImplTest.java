package event;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.categories.Category;
import ru.practicum.categories.CategoryRepository;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.*;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.requests.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.stats.StatsClient;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceImplTest {

    @InjectMocks
    private EventServiceImpl eventService;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private EventMapper eventMapper;

    @Mock
    private RequestMapper requestMapper;

    @Mock
    private UserRepository userRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private StatsClient statsClient;

    @Mock
    private HttpServletRequest httpServletRequest;

    private Event event;
    private User user;
    private Category category;
    private NewEventDto newEventDto;
    private UpdateEventAdminRequest updateEventAdminRequest;
    private UpdateEventUserRequest updateEventUserRequest;
    private EventRequestStatusUpdateRequest statusUpdateRequest;

    @BeforeEach
    void setUp() {
        user = new User(1L, "Test User", "test@example.com");
        category = new Category(1L, "Test Category");
        event = Event.builder()
                .id(1L)
                .initiator(user)
                .category(category)
                .state(Status.PENDING)
                .eventDate(LocalDateTime.now().plusDays(1))
                .annotation("Test Event")
                .description("Test Description")
                .title("Test Title")
                .paid(false)
                .participantLimit(10)
                .requestModeration(true)
                .confirmedRequests(0)
                .build();

        newEventDto = new NewEventDto();
        newEventDto.setCategory(1L);
        newEventDto.setEventDate(LocalDateTime.now().plusDays(1));
        newEventDto.setAnnotation("Test Event");
        newEventDto.setDescription("Test Description");
        newEventDto.setTitle("Test Title");
        newEventDto.setPaid(false);
        newEventDto.setParticipantLimit(10);
        newEventDto.setRequestModeration(true);

        updateEventAdminRequest = new UpdateEventAdminRequest();
        updateEventAdminRequest.setEventDate(LocalDateTime.now().plusDays(2));

        updateEventUserRequest = new UpdateEventUserRequest();
        updateEventUserRequest.setEventDate(LocalDateTime.now().plusDays(2));

        statusUpdateRequest = new EventRequestStatusUpdateRequest();
        statusUpdateRequest.setRequestIds(List.of(1L));
        statusUpdateRequest.setStatus(RequestStatus.CONFIRMED);
    }

    @Test
    void getAllFilteredEvents() {
        List<Event> events = List.of(event);
        List<EventFullDto> eventFullDtos = List.of(new EventFullDto());
        Pageable pageable = PageRequest.of(0, 10);

        when(eventsRepository.getAllFilteredEvents(any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(events);
        when(eventMapper.toEventFullDtos(events)).thenReturn(eventFullDtos);

        List<EventFullDto> result = eventService.getAllFilteredEvents(
                List.of(1L), List.of("PENDING"), List.of(1L), null, null, 0, 10);

        assertEquals(eventFullDtos, result);
        verify(eventsRepository).getAllFilteredEvents(any(), any(), any(), any(), any(), eq(pageable));
        verify(eventMapper).toEventFullDtos(events);
    }

    @Test
    void getAllFilteredEventsEmptyList() {
        Pageable pageable = PageRequest.of(0, 10);

        when(eventsRepository.getAllFilteredEvents(any(), any(), any(), any(), any(), eq(pageable)))
                .thenReturn(Collections.emptyList());

        List<EventFullDto> result = eventService.getAllFilteredEvents(
                List.of(1L), List.of("PENDING"), List.of(1L), null, null, 0, 10);

        assertTrue(result.isEmpty());
        verify(eventsRepository).getAllFilteredEvents(any(), any(), any(), any(), any(), eq(pageable));
        verify(eventMapper, never()).toEventFullDtos(any());
    }

    @Test
    void updateFilteredEventValid() {
        EventFullDto eventFullDto = new EventFullDto();
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(eventsRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto result = eventService.updateFilteredEvent(1L, updateEventAdminRequest);

        assertEquals(eventFullDto, result);
        verify(eventsRepository).save(event);
        verify(eventMapper).toEventFullDto(event);
    }

    @Test
    void updateFilteredEvent_EventNotFound_ThrowsNotFoundException() {
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () ->
                eventService.updateFilteredEvent(1L, updateEventAdminRequest));
    }

    @Test
    void getAllPublicFilteredEvents() {

        EventShortDto eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setAnnotation("Test Event");
        List<Event> events = List.of(event);
        List<EventShortDto> eventShortDtos = List.of(eventShortDto);
        Pageable pageable = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "eventDate"));

        when(eventsRepository.findPublicFilteredEvents(
                eq("test"), eq(List.of(1L)), eq(false), any(LocalDateTime.class), any(LocalDateTime.class), eq(true)))
                .thenReturn(events);
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(eventMapper.toEventShortDtos(events)).thenReturn(eventShortDtos);

        List<EventShortDto> result = eventService.getAllPublicFilteredEvents(
                "test", List.of(1L), false, LocalDateTime.now(), LocalDateTime.now().plusDays(1),
                true, EventSort.EVENT_DATE, 0, 10, httpServletRequest);

        assertEquals(eventShortDtos, result);
        assertEquals(1, result.size());
        assertEquals("Test Event", result.get(0).getAnnotation());
        verify(statsClient, times(1)).addHit(any());
        verify(eventMapper).toEventShortDtos(events);
        verify(eventsRepository).findPublicFilteredEvents(
                eq("test"), eq(List.of(1L)), eq(false), any(LocalDateTime.class), any(LocalDateTime.class), eq(true));
    }

    @Test
    void getPublicFilteredEventById() {
        event.setState(Status.PUBLISHED);
        EventFullDto eventFullDto = new EventFullDto();
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(httpServletRequest.getRemoteAddr()).thenReturn("127.0.0.1");
        when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto result = eventService.getPublicFilteredEventById(1L, httpServletRequest);

        assertEquals(eventFullDto, result);
        verify(statsClient, times(1)).addHit(any());
        verify(eventMapper).toEventFullDto(event);
    }

    @Test
    void getPublicFilteredEventByIdNotPublished() {
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(CommonNotFoundException.class, () ->
                eventService.getPublicFilteredEventById(1L, httpServletRequest));
    }

    @Test
    void getAllUserEventsValid() {
        List<Event> events = List.of(event);
        List<EventShortDto> eventShortDtos = List.of(new EventShortDto());
        Pageable pageable = PageRequest.of(0, 10);

        when(eventsRepository.findAllByInitiatorId(1L, pageable)).thenReturn(events);
        when(eventMapper.toEventShortDtos(events)).thenReturn(eventShortDtos);

        List<EventShortDto> result = eventService.getAllUserEvents(1L, 0, 10);

        assertEquals(eventShortDtos, result);
        verify(eventsRepository).findAllByInitiatorId(1L, pageable);
        verify(eventMapper).toEventShortDtos(events);
    }

    @Test
    void getAllUserEvents() {
        Pageable pageable = PageRequest.of(0, 10);
        when(eventsRepository.findAllByInitiatorId(1L, pageable)).thenReturn(Collections.emptyList());

        List<EventShortDto> result = eventService.getAllUserEvents(1L, 0, 10);

        assertNull(result);
        verify(eventsRepository).findAllByInitiatorId(1L, pageable);
        verify(eventMapper, never()).toEventShortDtos(any());
    }

    @Test
    void getEventByUserAndIdValid() {
        EventFullDto eventFullDto = new EventFullDto();
        when(eventsRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
        when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto result = eventService.getEventByUserAndId(1L, 1L);

        assertEquals(eventFullDto, result);
        verify(eventMapper).toEventFullDto(event);
    }

    @Test
    void getEventByUserAndIdNotFound() {
        when(eventsRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () ->
                eventService.getEventByUserAndId(1L, 1L));
    }

    @Test
    void createEvent() {
        EventFullDto eventFullDto = new EventFullDto();
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(eventMapper.toEventFromNewDto(newEventDto)).thenReturn(event);
        when(eventsRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto result = eventService.createEvent(1L, newEventDto);

        assertEquals(eventFullDto, result);
        verify(eventsRepository).save(event);
        verify(eventMapper).toEventFullDto(event);
    }

    @Test
    void createEventInvalid() {
        newEventDto.setEventDate(LocalDateTime.now());
        assertThrows(CommonBadRequestException.class, () ->
                eventService.createEvent(1L, newEventDto));
    }

    @Test
    void getParticipantRequestByUserAndEventIds() {
        Request request = new Request();
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.of(request));
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        ParticipationRequestDto result = eventService.getParticipantRequestByUserAndEventIds(1L, 1L);

        assertEquals(requestDto, result);
        verify(requestMapper).toRequestDto(request);
    }

    @Test
    void getParticipantRequestByUserAndEventIdsNotFound() {
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () ->
                eventService.getParticipantRequestByUserAndEventIds(1L, 1L));
    }

    @Test
    void updateEventByUserAndEventIdsValid() {
        EventFullDto eventFullDto = new EventFullDto();
        when(eventsRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));
        when(eventsRepository.save(event)).thenReturn(event);
        when(eventMapper.toEventFullDto(event)).thenReturn(eventFullDto);

        EventFullDto result = eventService.updateEventByUserAndEventIds(1L, 1L, updateEventUserRequest);

        assertEquals(eventFullDto, result);
        verify(eventsRepository).save(event);
        verify(eventMapper).toEventFullDto(event);
    }

    @Test
    void updateEventByUserAndEventIdsPublished() {
        event.setPublishedOn(LocalDateTime.now());
        when(eventsRepository.findByIdAndInitiatorId(1L, 1L)).thenReturn(Optional.of(event));

        assertThrows(CommonBadRequestException.class, () ->
                eventService.updateEventByUserAndEventIds(1L, 1L, updateEventUserRequest));
    }

    @Test
    void updateEventStatus() {
        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        List<Request> requests = List.of(request);
        ParticipationRequestDto requestDto = new ParticipationRequestDto();
        EventRequestStatusUpdateResult expectedResult = new EventRequestStatusUpdateResult();
        expectedResult.setConfirmedRequests(List.of(requestDto));
        expectedResult.setRejectedRequests(new ArrayList<>());

        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByEventAndInitiator(1L, 1L)).thenReturn(requests);
        when(requestMapper.toRequestDtos(requests)).thenReturn(List.of(requestDto));
        when(requestRepository.saveAll(any())).thenReturn(requests);
        when(eventsRepository.save(event)).thenReturn(event);

        EventRequestStatusUpdateResult result = eventService.updateEventStatus(1L, 1L, statusUpdateRequest);

        assertEquals(expectedResult.getConfirmedRequests(), result.getConfirmedRequests());
        assertEquals(expectedResult.getRejectedRequests(), result.getRejectedRequests());
        verify(eventsRepository).save(event);
        verify(requestRepository).saveAll(any());
    }

    @Test
    void updateEventStatusNoModeration() {
        event.setRequestModeration(false);
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        EventRequestStatusUpdateResult result = eventService.updateEventStatus(1L, 1L, statusUpdateRequest);

        assertTrue(result.getConfirmedRequests().isEmpty());
        assertTrue(result.getRejectedRequests().isEmpty());
        verify(requestRepository, never()).findAllByEventAndInitiator(any(), any());
    }

    @Test
    void updateEventStatusParticipantLimitExceeded() {
        event.setConfirmedRequests(10);
        Request request = new Request();
        request.setId(1L);
        request.setStatus(RequestStatus.PENDING);
        statusUpdateRequest.setStatus(RequestStatus.CONFIRMED);
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.findAllByEventAndInitiator(1L, 1L)).thenReturn(List.of(request));

        assertThrows(CommonConflictException.class, () ->
                eventService.updateEventStatus(1L, 1L, statusUpdateRequest));
    }
}