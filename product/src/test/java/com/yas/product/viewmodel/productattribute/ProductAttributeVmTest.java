package com.yas.product.viewmodel.productattribute;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.viewmodel.productattribute.ProductAttributeVm;
import org.junit.jupiter.api.Test;

class ProductAttributeVmTest {

    @Test
    void from_model_maps_fields() {
        ProductAttribute attribute = ProductAttribute.builder().id(8L).name("Size").build();

        ProductAttributeVm result = ProductAttributeVm.fromModel(attribute);

        assertThat(result.id()).isEqualTo(8L);
        assertThat(result.name()).isEqualTo("Size");
    }
}
