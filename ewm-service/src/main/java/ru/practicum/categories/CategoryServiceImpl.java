package ru.practicum.categories;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.errors.CategoryConflictException;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.EventsRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;

    @Override
    @Transactional
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName().isBlank()) {
            throw new CommonBadRequestException("Field: name. Error: must not be blank. Value: null");
        }

        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new CategoryConflictException("Category with name " + newCategoryDto.getName() + " already exists.");
            /*
            *  throw new CommonConflictException("could not execute statement; SQL [n/a]; constraint [uq_category_name];" +
                    " nested exception is org.hibernate.exception.ConstraintViolationException:" +
                    " could not execute statement");
            * */
        }

        Category category = categoryMapper.toCategoryFromNewDto(newCategoryDto);
        Category categorySaved = categoryRepository.save(category);
        CategoryDto categoryDto = categoryMapper.toCategoryDto(categorySaved);

        return categoryDto;
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (categoryRepository.findById(catId).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + catId + " was not found");
        }

        if (categoryRepository.existsByName(categoryDto.getName())) {
            Long idWithName = categoryRepository.findByName(categoryDto.getName()).getId();
            if (!Objects.equals(idWithName, catId)) {
                throw new CategoryConflictException("Category with name " + categoryDto.getName() + " already exists.");
            }
        }
        Category categoryFound = categoryRepository.findById(catId).get();
        categoryFound.setName(categoryDto.getName());
        Category categorySaved = categoryRepository.save(categoryFound);
        CategoryDto categorySavedDto = categoryMapper.toCategoryDto(categorySaved);
        return categorySavedDto;
    }

    @Override
    public void deleteCategory(Long catId) {
        if (categoryRepository.findById(catId).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + catId + " was not found");
        }

        if (eventsRepository.existsByCategoryId(catId)) {
            throw new CommonConflictException("Event for category " + catId + " exists.");
        }
        categoryRepository.deleteById(catId);
    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + id + " was not found");
        }
        Category category = categoryRepository.findById(id).get();
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        return categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}
