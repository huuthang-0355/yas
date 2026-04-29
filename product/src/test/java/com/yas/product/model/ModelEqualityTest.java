package com.yas.product.model;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductTemplate;
import org.junit.jupiter.api.Test;

class ModelEqualityTest {

    @Test
    void brand_equals_and_hashcode_use_id() {
        Brand a = new Brand();
        a.setId(1L);
        Brand b = new Brand();
        b.setId(1L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void category_equals_and_hashcode_use_id() {
        Category a = new Category();
        a.setId(2L);
        Category b = new Category();
        b.setId(2L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_option_equals_and_hashcode_use_id() {
        ProductOption a = new ProductOption();
        a.setId(3L);
        ProductOption b = new ProductOption();
        b.setId(3L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_template_equals_and_hashcode_use_id() {
        ProductTemplate a = ProductTemplate.builder().id(4L).name("T1").build();
        ProductTemplate b = ProductTemplate.builder().id(4L).name("T2").build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_attribute_equals_and_hashcode_use_id() {
        ProductAttribute a = ProductAttribute.builder().id(5L).name("A1").build();
        ProductAttribute b = ProductAttribute.builder().id(5L).name("A2").build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_attribute_group_equals_and_hashcode_use_id() {
        ProductAttributeGroup a = new ProductAttributeGroup();
        a.setId(6L);
        ProductAttributeGroup b = new ProductAttributeGroup();
        b.setId(6L);

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_related_equals_and_hashcode_use_id() {
        ProductRelated a = ProductRelated.builder().id(7L).build();
        ProductRelated b = ProductRelated.builder().id(7L).build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }

    @Test
    void product_equals_and_hashcode_use_id() {
        Product a = Product.builder().id(8L).build();
        Product b = Product.builder().id(8L).build();

        assertThat(a).isEqualTo(b);
        assertThat(a.hashCode()).isEqualTo(b.hashCode());
    }
}
