package com.yas.product.viewmodel.brand;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.Brand;
import com.yas.product.viewmodel.brand.BrandPostVm;
import org.junit.jupiter.api.Test;

class BrandPostVmTest {

    @Test
    void to_model_sets_fields() {
        BrandPostVm vm = new BrandPostVm("Brand", "brand-slug", true);

        Brand result = vm.toModel();

        assertThat(result.getName()).isEqualTo("Brand");
        assertThat(result.getSlug()).isEqualTo("brand-slug");
        assertThat(result.isPublished()).isTrue();
    }
}
