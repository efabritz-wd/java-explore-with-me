package ru.practicum.events;

import org.springframework.stereotype.Component;
import ru.practicum.categories.Category;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.events.dto.EventFullDto;
import ru.practicum.events.dto.EventShortDto;
import ru.practicum.events.dto.NewEventDto;
import ru.practicum.users.User;
import ru.practicum.users.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    public EventFullDto toEventFullDto(Event event) {
        if (event == null) {
            return null;
        }
        EventFullDto dto = new EventFullDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(categoryToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setCreatedOn(event.getCreatedOn());
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(userToUserShortDto(event.getInitiator()));
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setPublishedOn(event.getPublishedOn());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setState(event.getState() != null ? event.getState().name() : null);
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());
        return dto;
    }


    public List<EventFullDto> toEventFullDtos(List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::toEventFullDto)
                .collect(Collectors.toList());
    }


    public Event toEventFromFullDto(EventFullDto eventDto) {
        if (eventDto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(categoryDtoToCategory(eventDto.getCategory()));
        event.setConfirmedRequests(eventDto.getConfirmedRequests());
        event.setCreatedOn(eventDto.getCreatedOn());
        event.setDescription(eventDto.getDescription());
        event.setEventDate(eventDto.getEventDate());
        event.setInitiator(userShortDtoToUser(eventDto.getInitiator()));
        event.setLocation(eventDto.getLocation());
        event.setPaid(eventDto.getPaid());
        event.setParticipantLimit(eventDto.getParticipantLimit());
        event.setPublishedOn(eventDto.getPublishedOn());
        event.setRequestModeration(eventDto.getRequestModeration());
        event.setState(eventDto.getState() != null ? Status.valueOf(eventDto.getState()) : Status.PENDING);
        event.setTitle(eventDto.getTitle());
        event.setViews(eventDto.getViews());
        return event;
    }


    public List<Event> toEventsFromFullDto(List<EventFullDto> eventDtos) {
        if (eventDtos == null) {
            return null;
        }
        return eventDtos.stream()
                .map(this::toEventFromFullDto)
                .collect(Collectors.toList());
    }


    public EventShortDto toEventShortDto(Event event) {
        if (event == null) {
            return null;
        }
        EventShortDto dto = new EventShortDto();
        dto.setId(event.getId());
        dto.setAnnotation(event.getAnnotation());
        dto.setCategory(categoryToCategoryDto(event.getCategory()));
        dto.setConfirmedRequests(event.getConfirmedRequests());
        dto.setEventDate(event.getEventDate());
        dto.setInitiator(userToUserShortDto(event.getInitiator()));
        dto.setPaid(event.getPaid());
        dto.setTitle(event.getTitle());
        dto.setViews(event.getViews());
        return dto;
    }


    public List<EventShortDto> toEventShortDtos(List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::toEventShortDto)
                .collect(Collectors.toList());
    }


    public Event toEventFromShortDto(EventShortDto eventDto) {
        if (eventDto == null) {
            return null;
        }
        Event event = new Event();
        event.setId(eventDto.getId());
        event.setAnnotation(eventDto.getAnnotation());
        event.setCategory(categoryDtoToCategory(eventDto.getCategory()));
        event.setConfirmedRequests(eventDto.getConfirmedRequests());
        event.setEventDate(eventDto.getEventDate());
        event.setInitiator(userShortDtoToUser(eventDto.getInitiator()));
        event.setPaid(eventDto.getPaid());
        event.setTitle(eventDto.getTitle());
        event.setViews(eventDto.getViews());
        event.setState(Status.PENDING);
        event.setParticipantLimit(0);
        event.setRequestModeration(true);
        return event;
    }

    public NewEventDto toEventNewDto(Event event) {
        if (event == null) {
            return null;
        }

        NewEventDto dto = new NewEventDto();
        dto.setCategory(event.getCategory() != null ? event.getCategory().getId() : null);
        dto.setDescription(event.getDescription());
        dto.setEventDate(event.getEventDate());
        dto.setLocation(event.getLocation());
        dto.setPaid(event.getPaid());
        dto.setParticipantLimit(event.getParticipantLimit());
        dto.setRequestModeration(event.getRequestModeration());
        dto.setTitle(event.getTitle());

        return dto;
    }

    public Event toEventFromNewDto(NewEventDto newEventDto) {
        if (newEventDto == null) {
            return null;
        }

        Event event = new Event();
        if (newEventDto.getCategory() != null) {
            Category category = new Category();
            category.setId(newEventDto.getCategory());
            event.setCategory(category);
        }
        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setLocation(newEventDto.getLocation());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setCreatedOn(LocalDateTime.now());
        event.setState(Status.PENDING);
        event.setConfirmedRequests(0);
        event.setViews(0);

        return event;
    }

    public List<NewEventDto> toEventNewDtos(List<Event> events) {
        if (events == null) {
            return null;
        }
        return events.stream()
                .map(this::toEventNewDto)
                .collect(Collectors.toList());
    }

    public List<Event> toEventFromNewDtos(List<NewEventDto> eventsDto) {
        if (eventsDto == null) {
            return null;
        }
        return eventsDto.stream()
                .map(this::toEventFromNewDto)
                .collect(Collectors.toList());
    }

    private CategoryDto categoryToCategoryDto(Category category) {
        if (category == null) {
            return null;
        }
        CategoryDto dto = new CategoryDto();
        dto.setId(category.getId());
        dto.setName(category.getName());
        return dto;
    }

    private Category categoryDtoToCategory(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }
        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());
        return category;
    }

    private UserShortDto userToUserShortDto(User user) {
        if (user == null) {
            return null;
        }
        UserShortDto dto = new UserShortDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        return dto;
    }

    private User userShortDtoToUser(UserShortDto userShortDto) {
        if (userShortDto == null) {
            return null;
        }
        User user = new User();
        user.setId(userShortDto.getId());
        user.setName(userShortDto.getName());
        return user;
    }
}