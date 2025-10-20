package ru.practicum.compilations;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.compilations.dto.CompilationDto;

import java.util.List;


@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/compilations")
public class CompilationPublicController {
    private final CompilationService compilationService;

    @GetMapping
    public List<CompilationDto> getPublicCompilations(@RequestParam Boolean pinned,
                                                      @RequestParam Integer from,
                                                      @RequestParam Integer size) {
        return compilationService.getPublicCompilations(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getPublicCompilationById(@PathVariable Long compId) {
        return compilationService.getPublicCompilationById(compId);
    }
}
