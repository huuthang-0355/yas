package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.model.enumeration.DimensionUnit;
import com.yas.product.repository.BrandRepository;
import com.yas.product.repository.CategoryRepository;
import com.yas.product.repository.ProductCategoryRepository;
import com.yas.product.repository.ProductImageRepository;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductOptionRepository;
import com.yas.product.repository.ProductOptionValueRepository;
import com.yas.product.repository.ProductRelatedRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private BrandRepository brandRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private ProductCategoryRepository productCategoryRepository;

    @Mock
    private ProductImageRepository productImageRepository;

    @Mock
    private ProductOptionRepository productOptionRepository;

    @Mock
    private ProductOptionValueRepository productOptionValueRepository;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @Mock
    private ProductRelatedRepository productRelatedRepository;

    @InjectMocks
    private ProductService productService;

    ProductPostVm productPostVm;
    Product savedProduct;

    @BeforeEach
    void setUp() {
        productPostVm = new ProductPostVm(
            "Product 1",
            "slug",
            1L,
            List.of(),
            "shortDesc",
            "desc",
            "spec",
            "sku",
            "gtin",
            10.0,
            DimensionUnit.CM,
            10.0,
            10.0,
            10.0,
            10.0,
            true,
            true,
            true,
            true,
            true,
            "metaTitle",
            "metaKeyword",
            "metaDescription",
            1L,
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            List.of(),
            1L
        );

        savedProduct = Product.builder()
            .id(1L)
            .name("Product 1")
            .slug("slug")
            .build();
    }

    @Test
    void createProduct_GivenValidData_ShouldSaveAndReturnProduct() {
        // Arrange
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // Act
        var result = productService.createProduct(productPostVm);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("slug");
        verify(productRepository).save(any(Product.class));
    }

    @Test
    void createProduct_LengthSmallerThanWidth_ThrowsBadRequestException() {
        // Arrange
        ProductPostVm invalidVm = new ProductPostVm(
            "Product 1", "slug", 1L, List.of(), "shortDesc", "desc", "spec",
            "sku", "gtin", 10.0, DimensionUnit.CM, 5.0, 20.0, 10.0,
            10.0, true, true, true, true, true,
            "metaTitle", "metaKeyword", "metaDescription", 1L,
            List.of(), List.of(), List.of(), List.of(), List.of(), 1L
        );

        // Act & Assert
        assertThrows(BadRequestException.class, () -> productService.createProduct(invalidVm));
    }

    @Test
    void createProduct_DuplicateSlug_ThrowsDuplicatedException() {
        // Arrange
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.of(savedProduct));

        // Act & Assert
        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }
}
