package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Category;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.category.CategoryGetDetailVm;
import com.yas.product.viewmodel.category.CategoryGetVm;
import com.yas.product.viewmodel.category.CategoryListGetVm;
import com.yas.product.viewmodel.category.CategoryPostVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private MediaService mediaService;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void getPageableCategories_whenCategoriesExist_thenReturnPageableCategories() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setSlug("category-1");
        category.setDisplayOrder((short) 1);
        
        Page<Category> categoryPage = new PageImpl<>(List.of(category));
        when(categoryRepository.findAll(any(Pageable.class))).thenReturn(categoryPage);

        CategoryListGetVm result = categoryService.getPageableCategories(0, 10);

        assertThat(result).isNotNull();
        assertThat(result.categoryContent()).hasSize(1);
        assertThat(result.categoryContent().get(0).name()).isEqualTo("Category 1");
    }

    @Test
    void create_whenParentIdIsNull_thenReturnSavedCategory() {
        CategoryPostVm postVm = new CategoryPostVm("Category 1", "category-1", "desc", null, "metaKey", "metaDesc", (short) 1, true, null);
        when(categoryRepository.findExistedName("Category 1", null)).thenReturn(null);
        
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Category 1");
        savedCategory.setDisplayOrder((short) 1);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.create(postVm);

        assertThat(result).isNotNull();
        assertThat(result.getName()).isEqualTo("Category 1");
        verify(categoryRepository).save(any(Category.class));
    }

    @Test
    void create_whenParentIdIsNotNull_thenReturnSavedCategory() {
        CategoryPostVm postVm = new CategoryPostVm("Category 1", "category-1", "desc", 2L, "metaKey", "metaDesc", (short) 1, true, null);
        when(categoryRepository.findExistedName("Category 1", null)).thenReturn(null);
        
        Category parentCategory = new Category();
        parentCategory.setId(2L);
        parentCategory.setDisplayOrder((short) 1);
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));
        
        Category savedCategory = new Category();
        savedCategory.setId(1L);
        savedCategory.setName("Category 1");
        savedCategory.setParent(parentCategory);
        savedCategory.setDisplayOrder((short) 1);
        
        when(categoryRepository.save(any(Category.class))).thenReturn(savedCategory);

        Category result = categoryService.create(postVm);

        assertThat(result).isNotNull();
        assertThat(result.getParent().getId()).isEqualTo(2L);
    }

    @Test
    void create_whenParentNotFound_thenThrowBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Category 1", "category-1", "desc", 2L, "metaKey", "metaDesc", (short) 1, true, null);
        when(categoryRepository.findExistedName("Category 1", null)).thenReturn(null);
        when(categoryRepository.findById(2L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> categoryService.create(postVm));
    }

    @Test
    void create_whenDuplicateName_thenThrowDuplicatedException() {
        CategoryPostVm postVm = new CategoryPostVm("Category 1", "category-1", "desc", null, "metaKey", "metaDesc", (short) 1, true, null);
        when(categoryRepository.findExistedName("Category 1", null)).thenReturn(new Category());

        assertThrows(DuplicatedException.class, () -> categoryService.create(postVm));
    }

    @Test
    void update_whenParentIdIsNull_thenUpdateSuccessfully() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "category-1", "desc", null, "metaKey", "metaDesc", (short) 1, true, null);
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setName("Old Category");
        existingCategory.setDisplayOrder((short) 1);
        
        when(categoryRepository.findExistedName("Updated Category", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));

        categoryService.update(postVm, 1L);

        assertThat(existingCategory.getName()).isEqualTo("Updated Category");
        assertThat(existingCategory.getParent()).isNull();
    }

    @Test
    void update_whenParentIdIsNotNull_thenUpdateSuccessfully() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "category-1", "desc", 2L, "metaKey", "metaDesc", (short) 1, true, null);
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setDisplayOrder((short) 1);
        
        Category parentCategory = new Category();
        parentCategory.setId(2L);
        parentCategory.setDisplayOrder((short) 1);
        
        when(categoryRepository.findExistedName("Updated Category", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        when(categoryRepository.findById(2L)).thenReturn(Optional.of(parentCategory));

        categoryService.update(postVm, 1L);

        assertThat(existingCategory.getParent()).isEqualTo(parentCategory);
    }

    @Test
    void update_whenParentIsItself_thenThrowBadRequestException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "category-1", "desc", 1L, "metaKey", "metaDesc", (short) 1, true, null);
        Category existingCategory = new Category();
        existingCategory.setId(1L);
        existingCategory.setDisplayOrder((short) 1);
        
        when(categoryRepository.findExistedName("Updated Category", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(existingCategory));
        
        Category parentCategory = new Category();
        parentCategory.setId(1L);
        parentCategory.setDisplayOrder((short) 1);
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(parentCategory));

        assertThrows(BadRequestException.class, () -> categoryService.update(postVm, 1L));
    }

    @Test
    void update_whenCategoryNotFound_thenThrowNotFoundException() {
        CategoryPostVm postVm = new CategoryPostVm("Updated Category", "category-1", "desc", null, "metaKey", "metaDesc", (short) 1, true, null);
        when(categoryRepository.findExistedName("Updated Category", 1L)).thenReturn(null);
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.update(postVm, 1L));
    }

    @Test
    void getCategoryById_whenImageIdIsNotNull_thenReturnDetail() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setImageId(10L);
        category.setDisplayOrder((short) 1);
        
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(category));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "caption", "fileName", "mediaType", "http://image-url"));

        CategoryGetDetailVm result = categoryService.getCategoryById(1L);

        assertThat(result).isNotNull();
        assertThat(result.categoryImage()).isNotNull();
        assertThat(result.categoryImage().url()).isEqualTo("http://image-url");
    }

    @Test
    void getCategoryById_whenCategoryNotFound_thenThrowNotFoundException() {
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> categoryService.getCategoryById(1L));
    }

    @Test
    void getCategories_whenImageIdIsNotNull_thenReturnCategoryGetVms() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setImageId(10L);
        category.setDisplayOrder((short) 1);
        
        Category parent = new Category();
        parent.setId(2L);
        category.setParent(parent);
        
        when(categoryRepository.findByNameContainingIgnoreCase("Category")).thenReturn(List.of(category));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "caption", "fileName", "mediaType", "http://image-url"));

        List<CategoryGetVm> result = categoryService.getCategories("Category");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).categoryImage().url()).isEqualTo("http://image-url");
        assertThat(result.get(0).parentId()).isEqualTo(2L);
    }

    @Test
    void getCategoryByIds_whenIdsProvided_thenReturnVms() {
        Category category = new Category();
        category.setId(1L);
        category.setName("Category 1");
        category.setDisplayOrder((short) 1);
        
        when(categoryRepository.findAllById(List.of(1L))).thenReturn(List.of(category));

        List<CategoryGetVm> result = categoryService.getCategoryByIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.get(0).name()).isEqualTo("Category 1");
    }

    @Test
    void getTopNthCategories_whenLimitProvided_thenReturnNames() {
        when(categoryRepository.findCategoriesOrderedByProductCount(any(Pageable.class))).thenReturn(List.of("Category 1"));

        List<String> result = categoryService.getTopNthCategories(5);

        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo("Category 1");
    }
}