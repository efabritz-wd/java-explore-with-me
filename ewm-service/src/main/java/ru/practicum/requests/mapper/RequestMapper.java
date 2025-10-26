package ru.practicum.requests.mapper;

import org.mapstruct.Mapper;
import ru.practicum.requests.Request;
import ru.practicum.requests.dto.ParticipationRequestDto;


import java.util.List;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    ParticipationRequestDto toRequestDto(Request request);

    List<ParticipationRequestDto> toRequestDtos(List<Request> requests);

    Request toRequest(ParticipationRequestDto participationRequestDto);

    List<Request> toRequestsFromDto(List<ParticipationRequestDto> userDtos);
}
