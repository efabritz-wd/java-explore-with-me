package ru.practicum.events.mapper;

import org.mapstruct.Mapper;
import ru.practicum.events.Event;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {
    EventFullDto toEventFullDto(Event event);

    List<EventFullDto> toEventFullDtos(List<Event> events);

    Event toEventFromFullDto(EventFullDto eventDto);

    List<Event> toEventsFromFullDto(List<EventFullDto> eventDtos);

    EventShortDto toEventShortDto(Event event);

    List<EventShortDto> toEventShortDtos(List<Event> events);

    Event toEventFromShortDto(EventShortDto eventDto);


    NewEventDto toEventNewDto(Event event);

    List<NewEventDto> toEventNewDtos(List<Event> events);

    Event toEventFromNewDto(EventShortDto eventDto);

}
