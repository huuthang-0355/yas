package com.yas.search.viewmodel;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.search.constant.enums.SortType;
import com.yas.search.model.Product;
import com.yas.search.model.ProductCriteriaDto;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;

class ProductViewModelMapperTest {

    @Test
    void testProductGetVmFromModel_mapsAllFields() {
        ZonedDateTime createdOn = ZonedDateTime.now();
        Product product = Product.builder()
            .id(10L)
            .name("Phone")
            .slug("phone")
            .price(99.9)
            .isAllowedToOrder(true)
            .isPublished(true)
            .isFeatured(false)
            .isVisibleIndividually(true)
            .thumbnailMediaId(999L)
            .createdOn(createdOn)
            .build();

        ProductGetVm vm = ProductGetVm.fromModel(product);

        assertThat(vm.id()).isEqualTo(10L);
        assertThat(vm.name()).isEqualTo("Phone");
        assertThat(vm.slug()).isEqualTo("phone");
        assertThat(vm.thumbnailId()).isEqualTo(999L);
        assertThat(vm.price()).isEqualTo(99.9);
        assertThat(vm.isAllowedToOrder()).isTrue();
        assertThat(vm.isPublished()).isTrue();
        assertThat(vm.isFeatured()).isFalse();
        assertThat(vm.isVisibleIndividually()).isTrue();
        assertThat(vm.createdOn()).isEqualTo(createdOn);
    }

    @Test
    void testProductNameGetVmFromModel_mapsName() {
        Product product = Product.builder().name("Nikon Camera").build();

        ProductNameGetVm vm = ProductNameGetVm.fromModel(product);

        assertThat(vm.name()).isEqualTo("Nikon Camera");
    }

    @Test
    void testRecordViewModels_accessorsReturnExpectedValues() {
        ProductNameGetVm productName = new ProductNameGetVm("A");
        ProductNameListVm nameListVm = new ProductNameListVm(List.of(productName));

        ProductListGetVm listVm = new ProductListGetVm(
            List.of(),
            1,
            12,
            100,
            9,
            false,
            Map.of("brands", Map.of("ACME", 2L))
        );

        ProductEsDetailVm detailVm = new ProductEsDetailVm(
            1L,
            "Name",
            "slug",
            10.0,
            true,
            true,
            true,
            false,
            5L,
            "Brand",
            List.of("Cat"),
            List.of("Attr")
        );

        ProductCriteriaDto criteriaDto = new ProductCriteriaDto(
            "kw",
            0,
            12,
            "brand",
            "category",
            "attribute",
            1.0,
            2.0,
            SortType.DEFAULT
        );

        assertThat(nameListVm.productNames()).containsExactly(productName);
        assertThat(listVm.pageNo()).isEqualTo(1);
        assertThat(listVm.pageSize()).isEqualTo(12);
        assertThat(listVm.totalElements()).isEqualTo(100);
        assertThat(listVm.totalPages()).isEqualTo(9);
        assertThat(listVm.isLast()).isFalse();
        assertThat(listVm.aggregations().get("brands")).containsEntry("ACME", 2L);

        assertThat(detailVm.id()).isEqualTo(1L);
        assertThat(detailVm.name()).isEqualTo("Name");
        assertThat(detailVm.slug()).isEqualTo("slug");
        assertThat(detailVm.price()).isEqualTo(10.0);
        assertThat(detailVm.isPublished()).isTrue();
        assertThat(detailVm.isVisibleIndividually()).isTrue();
        assertThat(detailVm.isAllowedToOrder()).isTrue();
        assertThat(detailVm.isFeatured()).isFalse();
        assertThat(detailVm.thumbnailMediaId()).isEqualTo(5L);
        assertThat(detailVm.brand()).isEqualTo("Brand");
        assertThat(detailVm.categories()).containsExactly("Cat");
        assertThat(detailVm.attributes()).containsExactly("Attr");

        assertThat(criteriaDto.keyword()).isEqualTo("kw");
        assertThat(criteriaDto.page()).isEqualTo(0);
        assertThat(criteriaDto.size()).isEqualTo(12);
        assertThat(criteriaDto.brand()).isEqualTo("brand");
        assertThat(criteriaDto.category()).isEqualTo("category");
        assertThat(criteriaDto.attribute()).isEqualTo("attribute");
        assertThat(criteriaDto.minPrice()).isEqualTo(1.0);
        assertThat(criteriaDto.maxPrice()).isEqualTo(2.0);
        assertThat(criteriaDto.sortType()).isEqualTo(SortType.DEFAULT);
    }
}
