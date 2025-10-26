package ru.practicum.compilations;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;
import ru.practicum.events.Event;
import ru.practicum.events.EventMapper;
import ru.practicum.events.dto.EventShortDto;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CompilationMapper {
    private final EventMapper eventMapper;

    public CompilationDto toCompilationDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        CompilationDto compilationDto = new CompilationDto();
        compilationDto.setId(compilation.getId());
        compilationDto.setPinned(compilation.getPinned());
        compilationDto.setTitle(compilation.getTitle());

        if (compilation.getEvents() != null) {
            List<EventShortDto> eventShortDtos = compilation.getEvents().stream()
                    .map(eventMapper::toEventShortDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            compilationDto.setEvents(eventShortDtos);
        } else {
            compilationDto.setEvents(new ArrayList<>());
        }

        return compilationDto;
    }


    public List<CompilationDto> toCompilationsDtos(List<Compilation> compilations) {
        if (compilations == null) {
            return new ArrayList<>();
        }

        return compilations.stream()
                .map(this::toCompilationDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public Compilation toCompilationFromDto(CompilationDto compilationDto) {
        if (compilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();
        compilation.setId(compilationDto.getId());
        compilation.setPinned(compilationDto.getPinned());
        compilation.setTitle(compilationDto.getTitle());

        if (compilationDto.getEvents() != null) {
            List<Event> events = compilationDto.getEvents().stream()
                    .map(eventMapper::toEventFromShortDto)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            compilation.setEvents(new HashSet<>(events));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return compilation;
    }


    public List<Compilation> toCompilationsFromDto(List<CompilationDto> compilationDtos) {
        if (compilationDtos == null) {
            return new ArrayList<>();
        }

        return compilationDtos.stream()
                .map(this::toCompilationFromDto)
                .filter(compilation -> compilation != null)
                .collect(Collectors.toList());
    }


    public NewCompilationDto toCompilationNewDto(Compilation compilation) {
        if (compilation == null) {
            return null;
        }

        NewCompilationDto newCompilationDto = new NewCompilationDto();
        newCompilationDto.setPinned(compilation.getPinned() != null ? compilation.getPinned() : false);
        newCompilationDto.setTitle(compilation.getTitle());

        if (compilation.getEvents() != null) {
            List<Long> eventIds = compilation.getEvents().stream()
                    .map(Event::getId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            newCompilationDto.setEvents(eventIds);
        } else {
            newCompilationDto.setEvents(new ArrayList<>());
        }

        return newCompilationDto;
    }


    public List<NewCompilationDto> toCompilationNewDtos(List<Compilation> compilations) {
        if (compilations == null) {
            return new ArrayList<>();
        }

        return compilations.stream()
                .map(this::toCompilationNewDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    public Compilation toCompilationFromNewDto(NewCompilationDto newCompilationDto) {
        if (newCompilationDto == null) {
            return null;
        }

        Compilation compilation = new Compilation();
        compilation.setPinned(newCompilationDto.getPinned() != null ? newCompilationDto.getPinned() : false);
        compilation.setTitle(newCompilationDto.getTitle());

        if (newCompilationDto.getEvents() != null) {
            List<Event> events = newCompilationDto.getEvents().stream()
                    .map(eventId -> {
                        Event event = new Event();
                        event.setId(eventId);
                        return event;
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());
            compilation.setEvents(new HashSet<>(events));
        } else {
            compilation.setEvents(new HashSet<>());
        }

        return compilation;
    }


    public List<Compilation> toCompilationsFromNewDto(List<NewCompilationDto> newCompilationDtos) {
        if (newCompilationDtos == null) {
            return new ArrayList<>();
        }

        return newCompilationDtos.stream()
                .map(this::toCompilationFromNewDto)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
}
