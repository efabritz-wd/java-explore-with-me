package request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.events.Status;
import ru.practicum.requests.*;
import ru.practicum.requests.dto.ParticipationRequestDto;
import ru.practicum.users.User;
import ru.practicum.users.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RequestServiceImplTest {

    @InjectMocks
    private RequestServiceImpl requestService;

    @Mock
    private RequestRepository requestRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private EventsRepository eventsRepository;

    @Mock
    private RequestMapper requestMapper;

    private User user;
    private Event event;
    private Request request;
    private ParticipationRequestDto requestDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setName("Test User");
        user.setEmail("test@example.com");

        event = Event.builder()
                .id(1L)
                .initiator(new User(2L, "Other User", "other@example.com"))
                .publishedOn(LocalDateTime.now().minusDays(1))
                .participantLimit(10)
                .confirmedRequests(5)
                .requestModeration(true)
                .state(Status.PUBLISHED)
                .build();

        request = new Request();
        request.setId(1L);
        request.setRequester(1L);
        request.setEvent(1L);
        request.setCreated(LocalDateTime.now());
        request.setStatus(RequestStatus.PENDING);

        requestDto = new ParticipationRequestDto();
        requestDto.setId(1L);
        requestDto.setRequester(1L);
        requestDto.setEvent(1L);
        requestDto.setRequestStatus(RequestStatus.PENDING);
    }

    @Test
    void getRequestsByUserId() {
        List<Request> requests = List.of(request);
        List<ParticipationRequestDto> requestDtos = List.of(requestDto);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequester(1L)).thenReturn(requests);
        when(requestMapper.toRequestDtos(requests)).thenReturn(requestDtos);

        List<ParticipationRequestDto> result = requestService.getRequestsByUserId(1L);

        assertEquals(requestDtos, result);
        assertEquals(1, result.size());
        assertEquals(requestDto.getId(), result.get(0).getId());
        verify(userRepository).findById(1L);
        verify(requestRepository).findAllByRequester(1L);
        verify(requestMapper).toRequestDtos(requests);
    }

    @Test
    void getRequestsByUserIdEmptyList() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(requestRepository.findAllByRequester(1L)).thenReturn(Collections.emptyList());

        List<ParticipationRequestDto> result = requestService.getRequestsByUserId(1L);

        assertTrue(result.isEmpty());
        verify(userRepository).findById(1L);
        verify(requestRepository).findAllByRequester(1L);
        verify(requestMapper, never()).toRequestDtos(any());
    }

    @Test
    void getRequestsByUserIdUserNotFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> requestService.getRequestsByUserId(1L));
        verify(userRepository).findById(1L);
        verifyNoInteractions(requestRepository, requestMapper);
    }

    @Test
    void postNewRequestValid() {
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        ParticipationRequestDto result = requestService.postNewRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(requestDto, result);
        assertEquals(RequestStatus.PENDING, result.getRequestStatus());
        verify(requestRepository).save(any(Request.class));
        verify(requestMapper).toRequestDto(request);
    }

    @Test
    void postNewRequestExistingRequestThrowsConflictException() {
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.of(request));

        assertThrows(CommonConflictException.class, () -> requestService.postNewRequest(1L, 1L));
        verifyNoInteractions(eventsRepository, requestMapper);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void postNewRequestEventNotFound() {
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> requestService.postNewRequest(1L, 1L));
        verifyNoInteractions(requestMapper);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void postNewRequestUnpublishedEvent() {
        event.setPublishedOn(null);
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(CommonConflictException.class, () -> requestService.postNewRequest(1L, 1L));
        verifyNoInteractions(requestMapper);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void postNewRequestInitiatorAsRequester() {
        event.setInitiator(user);
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(CommonConflictException.class, () -> requestService.postNewRequest(1L, 1L));
        verifyNoInteractions(requestMapper);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void postNewRequestParticipantLimitReached() {
        event.setConfirmedRequests(10);
        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));

        assertThrows(CommonConflictException.class, () -> requestService.postNewRequest(1L, 1L));
        verifyNoInteractions(requestMapper);
        verify(requestRepository, never()).save(any());
    }

    @Test
    void postNewRequestNoModeration() {
        event.setRequestModeration(false);
        request.setStatus(RequestStatus.CONFIRMED);
        requestDto.setRequestStatus(RequestStatus.CONFIRMED);

        when(requestRepository.getRequestByEventAndRequester(1L, 1L)).thenReturn(Optional.empty());
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(requestRepository.save(any(Request.class))).thenReturn(request);
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        ParticipationRequestDto result = requestService.postNewRequest(1L, 1L);

        assertNotNull(result);
        assertEquals(RequestStatus.CONFIRMED, result.getRequestStatus());
        verify(requestRepository).save(any(Request.class));
        verify(requestMapper).toRequestDto(request);
    }

    @Test
    void cancelRequestsByUserIdAndEventId() {
        request.setStatus(RequestStatus.CANCELED);
        requestDto.setRequestStatus(RequestStatus.CANCELED);

        when(requestRepository.findByIdAndRequester(1L, 1L)).thenReturn(Optional.of(request));
        when(requestRepository.save(request)).thenReturn(request);
        when(requestMapper.toRequestDto(request)).thenReturn(requestDto);

        ParticipationRequestDto result = requestService.cancelRequestsByUserIdAndEventId(1L, 1L);

        assertNotNull(result);
        assertEquals(requestDto, result);
        assertEquals(RequestStatus.CANCELED, result.getRequestStatus());
        verify(requestRepository).save(request);
        verify(requestMapper).toRequestDto(request);
    }

    @Test
    void cancelRequestsByUserIdAndEventIdequestNotFound() {
        when(requestRepository.findByIdAndRequester(1L, 1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () ->
                requestService.cancelRequestsByUserIdAndEventId(1L, 1L));
        verify(requestRepository, never()).save(any());
        verifyNoInteractions(requestMapper);
    }
}
