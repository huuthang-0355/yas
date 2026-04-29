package com.yas.product.viewmodel.product;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.viewmodel.product.ProductCheckoutListVm;
import com.yas.product.viewmodel.product.ProductGetDetailVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListVm;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;

class ProductViewModelTest {

    @Test
    void product_get_detail_vm_from_model_maps_fields() {
        Product product = Product.builder().id(10L).name("P10").slug("p10").build();

        ProductGetDetailVm result = ProductGetDetailVm.fromModel(product);

        assertThat(result.id()).isEqualTo(10L);
        assertThat(result.name()).isEqualTo("P10");
        assertThat(result.slug()).isEqualTo("p10");
    }

    @Test
    void product_list_vm_from_model_maps_parent_id() {
        Product parent = Product.builder().id(20L).build();
        Product product = Product.builder().id(21L).name("P21").slug("p21").parent(parent).build();

        ProductListVm result = ProductListVm.fromModel(product);

        assertThat(result.parentId()).isEqualTo(20L);
    }

    @Test
    void product_checkout_list_vm_from_model_maps_brand_and_parent() {
        Brand brand = new Brand();
        brand.setId(1L);

        Product parent = Product.builder().id(30L).build();
        Product product = Product.builder()
            .id(31L)
            .name("P31")
            .description("desc")
            .shortDescription("short")
            .sku("sku-31")
            .brand(brand)
            .price(10.0)
            .taxClassId(2L)
            .parent(parent)
            .build();

        ProductCheckoutListVm result = ProductCheckoutListVm.fromModel(product);

        assertThat(result.brandId()).isEqualTo(1L);
        assertThat(result.parentId()).isEqualTo(30L);
        assertThat(result.thumbnailUrl()).isEmpty();
    }

    @Test
    void product_info_vm_from_product_maps_fields() {
        Product product = Product.builder().id(40L).name("P40").sku("sku-40").build();

        ProductInfoVm result = ProductInfoVm.fromProduct(product);

        assertThat(result.id()).isEqualTo(40L);
        assertThat(result.name()).isEqualTo("P40");
        assertThat(result.sku()).isEqualTo("sku-40");
    }
}
