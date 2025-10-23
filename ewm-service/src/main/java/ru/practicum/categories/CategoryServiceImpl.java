package ru.practicum.categories;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;
import ru.practicum.events.EventsRepository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class CategoryServiceImpl implements CategoryService {
    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final EventsRepository eventsRepository;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        if (newCategoryDto.getName().isBlank()) {
            throw new CommonBadRequestException("Field: name. Error: must not be blank. Value: null");
        }

        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new CommonConflictException("Category with name " + newCategoryDto.getName() + " already exists.");
        }

        Category category = categoryMapper.toCategoryFromNewDto(newCategoryDto);
        Category categorySaved = categoryRepository.save(category);
        log.info("Category created: " + categorySaved);
        CategoryDto categoryDto = categoryMapper.toCategoryDto(categorySaved);

        return categoryDto;
    }

    @Override
    public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
        if (categoryRepository.findById(catId).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + catId + " was not found");
        }

        if (categoryRepository.existsByName(categoryDto.getName())) {
            Long idWithName = categoryRepository.findByName(categoryDto.getName()).getId();
            if (!Objects.equals(idWithName, catId)) {
                throw new CommonConflictException("Category with name " + categoryDto.getName() + " already exists.");
            }
        }
        Category categoryFound = categoryRepository.findById(catId).get();
        categoryFound.setName(categoryDto.getName());
        Category categorySaved = categoryRepository.save(categoryFound);
        log.info("Category updated: " + categorySaved);
        CategoryDto categorySavedDto = categoryMapper.toCategoryDto(categorySaved);
        return categorySavedDto;
    }

    @Override
    public void deleteCategory(Long catId) {
        if (categoryRepository.findById(catId).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + catId + " was not found");
        }

        if (!eventsRepository.findAllByCategoryId(catId).isEmpty()) {
            throw new CommonConflictException("Event for category " + catId + " exists.");
        }

        categoryRepository.deleteById(catId);
        log.info("Deleted category with id: " + catId);

    }

    @Override
    public CategoryDto getCategoryById(Long id) {
        if (categoryRepository.findById(id).isEmpty()) {
            throw new CommonNotFoundException("Category with id="
                    + id + " was not found");
        }
        Category category = categoryRepository.findById(id).get();
        log.info("Category found: " + category);
        CategoryDto categoryDto = categoryMapper.toCategoryDto(category);
        return categoryDto;
    }

    @Override
    public List<CategoryDto> getAllCategories(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from / size, size);

        List<Category> categories = categoryRepository.findAll(pageable).getContent();

        log.info("Categories found: " + categories);

        return categories.stream()
                .map(category -> new CategoryDto(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}
