package com.yas.product.viewmodel.producttemplate;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.attribute.ProductTemplate;
import com.yas.product.viewmodel.producttemplate.ProductTemplateGetVm;
import org.junit.jupiter.api.Test;

class ProductTemplateGetVmTest {

    @Test
    void from_model_maps_fields() {
        ProductTemplate template = ProductTemplate.builder().id(5L).name("Template").build();

        ProductTemplateGetVm result = ProductTemplateGetVm.fromModel(template);

        assertThat(result.id()).isEqualTo(5L);
        assertThat(result.name()).isEqualTo("Template");
    }
}
