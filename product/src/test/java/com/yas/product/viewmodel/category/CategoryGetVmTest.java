package com.yas.product.viewmodel.category;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.Category;
import com.yas.product.viewmodel.category.CategoryGetVm;
import org.junit.jupiter.api.Test;

class CategoryGetVmTest {

    @Test
    void from_model_sets_parent_id_when_missing() {
        Category category = new Category();
        category.setId(2L);
        category.setName("Cat");
        category.setSlug("cat");

        CategoryGetVm result = CategoryGetVm.fromModel(category);

        assertThat(result.id()).isEqualTo(2L);
        assertThat(result.parentId()).isEqualTo(-1L);
    }

    @Test
    void from_model_sets_parent_id_when_present() {
        Category parent = new Category();
        parent.setId(99L);

        Category category = new Category();
        category.setId(3L);
        category.setName("Cat2");
        category.setSlug("cat2");
        category.setParent(parent);

        CategoryGetVm result = CategoryGetVm.fromModel(category);

        assertThat(result.parentId()).isEqualTo(99L);
    }
}
