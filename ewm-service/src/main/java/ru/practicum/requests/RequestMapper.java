package ru.practicum.requests;

import org.springframework.stereotype.Component;
import ru.practicum.requests.dto.ParticipationRequestDto;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class RequestMapper {

    public ParticipationRequestDto toRequestDto(Request request) {
        if (request == null) {
            return null;
        }

        ParticipationRequestDto dto = new ParticipationRequestDto();
        dto.setId(request.getId());
        dto.setEvent(request.getEvent());
        dto.setCreated(request.getCreated());
        dto.setRequester(request.getRequester());
        dto.setRequestStatus(request.getStatus());

        return dto;
    }


    public List<ParticipationRequestDto> toRequestDtos(List<Request> requests) {
        if (requests == null) {
            return null;
        }
        return requests.stream()
                .map(this::toRequestDto)
                .collect(Collectors.toList());
    }


    public Request toRequest(ParticipationRequestDto participationRequestDto) {
        if (participationRequestDto == null) {
            return null;
        }

        Request request = new Request();
        request.setId(participationRequestDto.getId());
        request.setEvent(participationRequestDto.getEvent());
        request.setCreated(participationRequestDto.getCreated());
        request.setRequester(participationRequestDto.getRequester());
        request.setStatus(participationRequestDto.getRequestStatus());

        return request;
    }


    public List<Request> toRequestsFromDto(List<ParticipationRequestDto> dtos) {
        if (dtos == null) {
            return null;
        }
        return dtos.stream()
                .map(this::toRequest)
                .collect(Collectors.toList());
    }
}