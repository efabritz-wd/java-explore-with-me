package ru.practicum.categories;

import org.springframework.stereotype.Component;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class CategoryMapper {

    public CategoryDto toCategoryDto(Category category) {
        if (category == null) {
            return null;
        }

        CategoryDto categoryDto = new CategoryDto();
        categoryDto.setId(category.getId());
        categoryDto.setName(category.getName());

        return categoryDto;
    }


    public List<CategoryDto> toCategoriesDtos(List<Category> categories) {
        if (categories == null) {
            return null;
        }

        List<CategoryDto> list = new ArrayList<>(categories.size());
        for (Category category : categories) {
            list.add(toCategoryDto(category));
        }

        return list;
    }


    public Category toCategoryFromDto(CategoryDto categoryDto) {
        if (categoryDto == null) {
            return null;
        }

        Category category = new Category();
        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        return category;
    }


    public List<Category> toCategoriesFromDto(List<CategoryDto> categoryDtos) {
        if (categoryDtos == null) {
            return null;
        }

        List<Category> list = new ArrayList<>(categoryDtos.size());
        for (CategoryDto categoryDto : categoryDtos) {
            list.add(toCategoryFromDto(categoryDto));
        }

        return list;
    }


    public NewCategoryDto toCategoryNewDto(Category category) {
        if (category == null) {
            return null;
        }

        NewCategoryDto newCategoryDto = new NewCategoryDto();
        newCategoryDto.setName(category.getName());

        return newCategoryDto;
    }


    public List<NewCategoryDto> toCategoryNewDtos(List<Category> categories) {
        if (categories == null) {
            return null;
        }

        List<NewCategoryDto> list = new ArrayList<>(categories.size());
        for (Category category : categories) {
            list.add(toCategoryNewDto(category));
        }

        return list;
    }


    public Category toCategoryFromNewDto(NewCategoryDto newCategoryDto) {
        if (newCategoryDto == null) {
            return null;
        }

        Category category = new Category();
        category.setName(newCategoryDto.getName());

        return category;
    }


    public List<Category> toCategoriesFromNewDto(List<NewCategoryDto> newCategoryDtos) {
        if (newCategoryDtos == null) {
            return null;
        }

        List<Category> list = new ArrayList<>(newCategoryDtos.size());
        for (NewCategoryDto newCategoryDto : newCategoryDtos) {
            list.add(toCategoryFromNewDto(newCategoryDto));
        }

        return list;
    }
}
