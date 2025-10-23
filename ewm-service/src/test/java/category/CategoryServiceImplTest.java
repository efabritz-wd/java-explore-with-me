package category;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.categories.*;
import ru.practicum.categories.dto.CategoryDto;
import ru.practicum.categories.dto.NewCategoryDto;
import ru.practicum.errors.CommonBadRequestException;
import ru.practicum.errors.CommonConflictException;
import ru.practicum.errors.CommonNotFoundException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest(
        properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;MODE=PostgreSQL",
                "spring.datasource.driverClassName=org.h2.Driver",
                "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect",
                "spring.jpa.hibernate.ddl-auto=create-drop"
        },
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@Transactional
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@ContextConfiguration(classes = ru.practicum.EwmServiceApp.class)
public class CategoryServiceImplTest {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryMapper categoryMapper;

    private NewCategoryDto newCategoryDto;
    private NewCategoryDto newCategoryDto2;
    private NewCategoryDto newCategoryDto3;

    @BeforeEach
    void setUp() {
        categoryRepository.deleteAll();

        newCategoryDto = new NewCategoryDto("Kino");
        newCategoryDto2 = new NewCategoryDto("Sport");
        newCategoryDto3 = new NewCategoryDto("Theater");

        Category cd = categoryMapper.toCategoryFromNewDto(newCategoryDto);

        categoryRepository.save(categoryMapper.toCategoryFromNewDto(newCategoryDto));
        categoryRepository.save(categoryMapper.toCategoryFromNewDto(newCategoryDto2));
        categoryRepository.save(categoryMapper.toCategoryFromNewDto(newCategoryDto3));
    }

    @Test
    void createValidCategory() {
        NewCategoryDto newDto = new NewCategoryDto("Treffen");

        CategoryDto result = categoryService.createCategory(newDto);

        assertNotNull(result);
        assertNotNull(result.getId());
        assertEquals(newDto.getName(), result.getName());
        assertTrue(categoryRepository.existsByName(newDto.getName()));
    }

    @Test
    void createBlankCategory() {
        NewCategoryDto blankNameDto = new NewCategoryDto("  ");

        CommonBadRequestException exception = assertThrows(CommonBadRequestException.class,
                () -> categoryService.createCategory(blankNameDto));
        assertEquals("Field: name. Error: must not be blank. Value: null", exception.getMessage());
        assertFalse(categoryRepository.existsByName("  "));
    }

    @Test
    void createDuplicateCategory() {
        NewCategoryDto duplicateDto = new NewCategoryDto("Kino");

        CommonConflictException exception = assertThrows(CommonConflictException.class,
                () -> categoryService.createCategory(duplicateDto));
        assertEquals("Category with name " + duplicateDto.getName() + " already exists.", exception.getMessage());
    }

    @Test
    void updateCategory() {
        Category existingCategory = categoryRepository.findAll().get(0);
        CategoryDto updateDto = new CategoryDto(existingCategory.getId(), "Updated Concert");

        CategoryDto result = categoryService.updateCategory(existingCategory.getId(), updateDto);

        assertNotNull(result);
        assertEquals(updateDto.getId(), result.getId());
        assertEquals(updateDto.getName(), result.getName());
        Category updatedCategory = categoryRepository.findById(existingCategory.getId()).orElseThrow();
        assertEquals("Updated Concert", updatedCategory.getName());
    }

    @Test
    void updateNonExistingCategory() {
        CategoryDto updateDto = new CategoryDto(5555L, "CategoryNew");

        CommonNotFoundException exception = assertThrows(CommonNotFoundException.class,
                () -> categoryService.updateCategory(5555L, updateDto));
        assertEquals("Category with id=5555 was not found", exception.getMessage());
    }

    @Test
    void deleteCategory() {
        Category existingCategory = categoryRepository.findAll().get(0);

        categoryService.deleteCategory(existingCategory.getId());

        assertFalse(categoryRepository.existsById(existingCategory.getId()));
    }

    @Test
    void deleteNonExistingCategory() {
        Long nonExistentId = 999L;

        CommonNotFoundException exception = assertThrows(CommonNotFoundException.class,
                () -> categoryService.deleteCategory(nonExistentId));
        assertEquals("Category with id=999 was not found", exception.getMessage());
    }

    @Test
    void getCategoryById() {
        Category existingCategory = categoryRepository.findAll().get(0);

        CategoryDto result = categoryService.getCategoryById(existingCategory.getId());

        assertNotNull(result);
        assertEquals(existingCategory.getId(), result.getId());
        assertEquals(existingCategory.getName(), result.getName());
    }

    @Test
    void getCategoryByNonExistingId() {
        Long nonExistentId = 5555L;

        CommonNotFoundException exception = assertThrows(CommonNotFoundException.class,
                () -> categoryService.getCategoryById(nonExistentId));
        assertEquals("Category with id=5555 was not found", exception.getMessage());
    }

    @Test
    void getAllCategories() {
        int from = 0;
        int size = 2;

        List<CategoryDto> result = categoryService.getAllCategories(from, size);

        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("Kino", result.get(0).getName());
        assertEquals("Sport", result.get(1).getName());
    }

    @Test
    void getAllCategoriesOfEmpty() {
        categoryRepository.deleteAll();
        int from = 0;
        int size = 10;

        List<CategoryDto> result = categoryService.getAllCategories(from, size);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}