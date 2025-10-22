package ru.practicum.compilations;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.requests.UpdateCompilationRequest;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/admin/compilations")
public class CompilationAdminController {
    private final CompilationService compilationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto postCompilation(@Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.postCompilation(updateCompilationRequest);
    }

    @PatchMapping("/{compId}")
    public CompilationDto patchCompilation(@PathVariable Long compId,
                                           @Valid @RequestBody UpdateCompilationRequest updateCompilationRequest) {
        return compilationService.patchCompilation(compId, updateCompilationRequest);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompilation(@PathVariable Long compId) {
        compilationService.deleteCompilation(compId);
    }
}
