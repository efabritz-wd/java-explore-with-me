package ru.practicum.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.Event;
import ru.practicum.events.EventsRepository;
import ru.practicum.requests.UpdateCompilationRequest;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class CompilationServiceImpl implements CompilationService {
    private final CompilationRepository compilationRepository;
    private final CompilationMapper compilationMapper;
    private final EventsRepository eventsRepository;

    @Transactional(readOnly = true)
    @Override
    public List<CompilationDto> getPublicCompilations(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);
        List<Compilation> compilations = compilationRepository.getPublicCompilations(pinned, pageable);
        if (compilations.isEmpty()) {
            return List.of();
        }
        return compilationMapper.toCompilationsDtos(compilations);
    }

    @Transactional(readOnly = true)
    @Override
    public CompilationDto getPublicCompilationById(Long compId) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CommonNotFoundException("Compilation with id " + compId + " was not found."));

        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto postCompilation(UpdateCompilationRequest newCompilationDto) {
        if (newCompilationDto.getTitle() == null) {
            throw new CommonBadRequestException("Compilation title is empty");
        }
        Compilation compilation = new Compilation();

        if (newCompilationDto.getTitle().isBlank()) {
            throw new CommonBadRequestException("Compilation title cannot be blank.");
        }

        compilation.setTitle(newCompilationDto.getTitle());

        Set<Event> eventSet = new HashSet<>();
        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() == null || newCompilationDto.getEvents().isEmpty()) {
            compilation.setEvents(new HashSet<>());
        } else {
            events = newCompilationDto.getEvents().stream()
                    .map(eventId -> eventsRepository.findById(eventId)
                            .orElseThrow(() -> new CommonNotFoundException("Event not found with ID: " + eventId)))
                    .toList();
        }
        compilation.setPinned(newCompilationDto.getPinned());

        eventSet.addAll(events);
        compilation.setEvents(eventSet);
        compilationRepository.save(compilation);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public CompilationDto patchCompilation(Long compId, UpdateCompilationRequest newCompilationDto) {
        Compilation compilation = compilationRepository.findById(compId).orElseThrow(() ->
                new CommonNotFoundException("Compilation with id " + compId + " not found."));

        if (newCompilationDto.getTitle() != null)
            compilation.setTitle(newCompilationDto.getTitle());

        if (newCompilationDto.getPinned() != null)
            compilation.setPinned(newCompilationDto.getPinned());

        List<Event> events = new ArrayList<>();

        if (newCompilationDto.getEvents() == null) {
            compilation.setEvents(new HashSet<>());
        } else {
            events = newCompilationDto.getEvents().stream()
                    .map(eventId -> eventsRepository.findById(eventId)
                            .orElseThrow(() -> new CommonNotFoundException("Event not found with ID: " + eventId)))
                    .toList();
        }
        Set<Event> eventSet = new HashSet<>(events);
        compilation.setEvents(eventSet);
        return compilationMapper.toCompilationDto(compilation);
    }

    @Override
    public void deleteCompilation(Long compId) {
        compilationRepository.findById(compId).orElseThrow(() ->
                new CommonNotFoundException("Compilation with id " + compId + " not found."));
        compilationRepository.deleteById(compId);
    }
}
