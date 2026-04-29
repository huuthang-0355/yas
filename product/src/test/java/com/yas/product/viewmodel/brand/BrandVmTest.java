package com.yas.product.viewmodel.brand;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.Brand;
import com.yas.product.viewmodel.brand.BrandVm;
import org.junit.jupiter.api.Test;

class BrandVmTest {

    @Test
    void from_model_maps_fields() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Brand");
        brand.setSlug("brand-slug");
        brand.setPublished(true);

        BrandVm result = BrandVm.fromModel(brand);

        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.name()).isEqualTo("Brand");
        assertThat(result.slug()).isEqualTo("brand-slug");
        assertThat(result.isPublish()).isTrue();
    }
}
