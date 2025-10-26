package ru.practicum.compilations;


import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.requests.UpdateCompilationRequest;

import java.util.List;

public interface CompilationService {
    List<CompilationDto> getPublicCompilations(Boolean pinned,
                                               Integer from,
                                               Integer size);

    CompilationDto getPublicCompilationById(Long compId);

    CompilationDto postCompilation(UpdateCompilationRequest newCompilationDto);

    CompilationDto patchCompilation(Long compId, UpdateCompilationRequest newCompilationDto);

    void deleteCompilation(Long compId);
}
