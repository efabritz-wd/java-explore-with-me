package ru.practicum.categories.mapper;

import org.mapstruct.Mapper;
import ru.practicum.categories.Category;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryDto toCategoryDto(Category category);

    List<CategoryDto> toCategoriesDtos(List<Category> categories);

    Category toCategoryFromDto(CategoryDto categoryDto);

    List<Category> toCategoriesFromDto(List<CategoryDto> categoryDtos);


    NewCategoryDto toCategoryNewDto(Category category);

    List<NewCategoryDto> toCategoryNewDtos(List<Category> categories);

    Category toCategoryFromNewDto(NewCategoryDto newCategoryDto);

    List<Category> toCategoriesFromNewDto(List<NewCategoryDto> newCategoryDtos);
}
