package com.yas.product.viewmodel.productoption;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.ProductOption;
import com.yas.product.viewmodel.productoption.ProductOptionGetVm;
import org.junit.jupiter.api.Test;

class ProductOptionGetVmTest {

    @Test
    void from_model_maps_fields() {
        ProductOption option = new ProductOption();
        option.setId(7L);
        option.setName("Color");

        ProductOptionGetVm result = ProductOptionGetVm.fromModel(option);

        assertThat(result.id()).isEqualTo(7L);
        assertThat(result.name()).isEqualTo("Color");
    }
}
