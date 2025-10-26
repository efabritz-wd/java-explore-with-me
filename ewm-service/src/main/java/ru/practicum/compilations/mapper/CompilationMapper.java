package ru.practicum.compilations.mapper;

import org.mapstruct.Mapper;
import ru.practicum.compilations.Compilation;
import ru.practicum.compilations.dto.CompilationDto;
import ru.practicum.compilations.dto.NewCompilationDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CompilationMapper {
    CompilationDto toCompilationDto(Compilation compilation);

    List<CompilationDto>  toCompilationsDtos(List<Compilation> compilations);

    Compilation  toCompilationFromDto(CompilationDto compilationDto);

    List<Compilation>  toCompilationsFromDto(List<CompilationDto> compilationDtos);


    NewCompilationDto  toCompilationNewDto(Compilation compilation);

    List<NewCompilationDto>  toCompilationNewDtos(List<Compilation> compilations);

    Compilation  toCompilationFromNewDto(NewCompilationDto newCompilationDto);

    List<Compilation>  toCompilationsFromNewDto(List<NewCompilationDto> newCompilationDtos);
}
