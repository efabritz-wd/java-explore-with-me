package ru.practicum.events;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class EventSortConverter implements Converter<String, EventSort> {
    @Override
    public EventSort convert(String source) {
        try {
            return EventSort.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            return EventSort.EVENT_DATE;
        }
    }
}
