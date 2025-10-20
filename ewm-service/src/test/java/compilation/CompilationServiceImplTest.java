package compilation;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.compilations.Compilation;
import ru.practicum.compilations.CompilationMapper;
import ru.practicum.compilations.CompilationRepository;
import ru.practicum.compilations.CompilationServiceImpl;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.requests.UpdateCompilationRequest;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompilationServiceImplTest {

    @InjectMocks
    private CompilationServiceImpl compilationService;

    @Mock
    private CompilationRepository compilationRepository;

    @Mock
    private CompilationMapper compilationMapper;

    @Mock
    private EventsRepository eventsRepository;

    private Compilation compilation;
    private CompilationDto compilationDto;
    private UpdateCompilationRequest newCompilationDto;
    private Event event;
    private EventShortDto eventShortDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        event = Event.builder()
                .id(1L)
                .title("Test Event")
                .build();

        eventShortDto = new EventShortDto();
        eventShortDto.setId(1L);
        eventShortDto.setTitle("Test Event");

        compilation = new Compilation();
        compilation.setId(1L);
        compilation.setTitle("Test Compilation");
        compilation.setPinned(true);
        compilation.setEvents(new HashSet<>(List.of(event)));

        compilationDto = new CompilationDto();
        compilationDto.setId(1L);
        compilationDto.setTitle("Test Compilation");
        compilationDto.setPinned(true);
        compilationDto.setEvents(List.of(eventShortDto)); // Corrected to List<EventShortDto>

        newCompilationDto = new UpdateCompilationRequest();
        newCompilationDto.setTitle("Test Compilation");
        newCompilationDto.setPinned(true);
        newCompilationDto.setEvents(List.of(1L)); // List<Long> for event IDs

        pageable = PageRequest.of(0, 10);
    }

    @Test
    void getPublicCompilations() {
        List<Compilation> compilations = List.of(compilation);
        List<CompilationDto> compilationDtos = List.of(compilationDto);

        when(compilationRepository.getPublicCompilations(true, pageable)).thenReturn(compilations);
        when(compilationMapper.toCompilationsDtos(compilations)).thenReturn(compilationDtos);

        List<CompilationDto> result = compilationService.getPublicCompilations(true, 0, 10);

        assertEquals(compilationDtos, result);
        assertEquals(1, result.size());
        assertEquals("Test Compilation", result.get(0).getTitle());
        assertEquals(1, result.get(0).getEvents().size());
        assertEquals("Test Event", result.get(0).getEvents().get(0).getTitle());
        verify(compilationRepository).getPublicCompilations(true, pageable);
        verify(compilationMapper).toCompilationsDtos(compilations);
    }

    @Test
    void getPublicCompilationsEmpty() {
        when(compilationRepository.getPublicCompilations(true, pageable)).thenReturn(Collections.emptyList());

        List<CompilationDto> result = compilationService.getPublicCompilations(true, 0, 10);

        assertTrue(result.isEmpty());
        verify(compilationRepository).getPublicCompilations(true, pageable);
        verify(compilationMapper, never()).toCompilationsDtos(any());
    }

    @Test
    void getPublicCompilationById() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(compilationDto);

        CompilationDto result = compilationService.getPublicCompilationById(1L);

        assertNotNull(result);
        assertEquals(compilationDto, result);
        assertEquals("Test Compilation", result.getTitle());
        assertEquals(1, result.getEvents().size());
        assertEquals("Test Event", result.getEvents().get(0).getTitle());
        verify(compilationRepository).findById(1L);
        verify(compilationMapper).toCompilationDto(compilation);
    }

    @Test
    void getPublicCompilationByIdNotFound() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> compilationService.getPublicCompilationById(1L));
        verify(compilationRepository).findById(1L);
        verifyNoInteractions(compilationMapper);
    }

    @Test
    void postCompilation() {
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(compilationMapper.toCompilationDto(any(Compilation.class))).thenReturn(compilationDto);

        CompilationDto result = compilationService.postCompilation(newCompilationDto);

        assertNotNull(result);
        assertEquals(compilationDto, result);
        assertEquals("Test Compilation", result.getTitle());
        assertEquals(true, result.getPinned());
        assertEquals(1, result.getEvents().size());
        assertEquals("Test Event", result.getEvents().get(0).getTitle());
        verify(eventsRepository).findById(1L);
        verify(compilationMapper).toCompilationDto(any(Compilation.class));
    }

    @Test
    void postCompilationEmpty() {
        newCompilationDto.setEvents(Collections.emptyList());
        CompilationDto emptyEventsDto = new CompilationDto();
        emptyEventsDto.setId(1L);
        emptyEventsDto.setTitle("Test Compilation");
        emptyEventsDto.setPinned(true);
        emptyEventsDto.setEvents(Collections.emptyList());

        when(compilationMapper.toCompilationDto(any(Compilation.class))).thenReturn(emptyEventsDto);

        CompilationDto result = compilationService.postCompilation(newCompilationDto);

        assertNotNull(result);
        assertEquals(emptyEventsDto, result);
        assertTrue(result.getEvents().isEmpty());
        verifyNoInteractions(eventsRepository);
        verify(compilationMapper).toCompilationDto(any(Compilation.class));
    }

    @Test
    void postCompilationBlankTitle() {
        newCompilationDto.setTitle("");

        assertThrows(CommonBadRequestException.class, () -> compilationService.postCompilation(newCompilationDto));
        verifyNoInteractions(eventsRepository, compilationMapper);
    }

    @Test
    void postCompilationNullPinned() {
        newCompilationDto.setPinned(null);

        assertThrows(CommonBadRequestException.class, () -> compilationService.postCompilation(newCompilationDto));
        verifyNoInteractions(eventsRepository, compilationMapper);
    }

    @Test
    void postCompilationEventNotFoundon() {
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> compilationService.postCompilation(newCompilationDto));
        verify(eventsRepository).findById(1L);
        verifyNoInteractions(compilationMapper);
    }

    @Test
    void patchCompilation() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(eventsRepository.findById(1L)).thenReturn(Optional.of(event));
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(compilationDto);

        CompilationDto result = compilationService.patchCompilation(1L, newCompilationDto);

        assertNotNull(result);
        assertEquals(compilationDto, result);
        assertEquals("Test Compilation", result.getTitle());
        assertEquals(1, result.getEvents().size());
        assertEquals("Test Event", result.getEvents().get(0).getTitle());
        verify(compilationRepository).findById(1L);
        verify(eventsRepository).findById(1L);
        verify(compilationMapper).toCompilationDto(compilation);
    }

    @Test
    void patchCompilationNullEvents() {
        newCompilationDto.setEvents(null);
        CompilationDto emptyEventsDto = new CompilationDto();
        emptyEventsDto.setId(1L);
        emptyEventsDto.setTitle("Test Compilation");
        emptyEventsDto.setPinned(true);
        emptyEventsDto.setEvents(Collections.emptyList());

        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(compilationMapper.toCompilationDto(compilation)).thenReturn(emptyEventsDto);

        CompilationDto result = compilationService.patchCompilation(1L, newCompilationDto);

        assertNotNull(result);
        assertEquals(emptyEventsDto, result);
        assertTrue(result.getEvents().isEmpty());
        verify(compilationRepository).findById(1L);
        verifyNoInteractions(eventsRepository);
        verify(compilationMapper).toCompilationDto(compilation);
    }

    @Test
    void patchCompilationNotFound() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> compilationService.patchCompilation(1L, newCompilationDto));
        verify(compilationRepository).findById(1L);
        verifyNoInteractions(eventsRepository, compilationMapper);
    }

    @Test
    void patchCompilationEventNotFound() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));
        when(eventsRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> compilationService.patchCompilation(1L, newCompilationDto));
        verify(compilationRepository).findById(1L);
        verify(eventsRepository).findById(1L);
        verifyNoInteractions(compilationMapper);
    }

    @Test
    void deleteCompilation() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.of(compilation));

        compilationService.deleteCompilation(1L);

        verify(compilationRepository).findById(1L);
        verify(compilationRepository).deleteById(1L);
        verifyNoInteractions(compilationMapper, eventsRepository);
    }

    @Test
    void deleteCompilation_NotFound_ThrowsNotFoundException() {
        when(compilationRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(CommonNotFoundException.class, () -> compilationService.deleteCompilation(1L));
        verify(compilationRepository).findById(1L);
        verify(compilationRepository, never()).deleteById(anyLong());
        verifyNoInteractions(compilationMapper, eventsRepository);
    }
}