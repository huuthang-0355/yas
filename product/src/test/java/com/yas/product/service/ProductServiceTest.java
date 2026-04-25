package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.BadRequestException;
import com.yas.commonlibrary.exception.DuplicatedException;
import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Category;
import com.yas.product.model.Product;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductRelated;
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
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
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
    ProductPutVm productPutVm;
    Product savedProduct;

    @BeforeEach
    void setUp() {
        productPostVm = new ProductPostVm(
            "Product 1", "slug", 1L, List.of(1L, 2L), "shortDesc", "desc", "spec",
            "sku", "gtin", 10.0, DimensionUnit.CM, 10.0, 10.0, 10.0,
            10.0, true, true, true, true, true,
            "metaTitle", "metaKeyword", "metaDescription", 1L,
            List.of(1L), List.of(), List.of(), List.of(), List.of(2L), 1L
        );
        productPutVm = new ProductPutVm(
            "Product 1 updated", "slug", 20.0, true, true, true, true, true, 1L, new ArrayList<>(List.of(1L, 2L)), "shortDesc", "desc", "spec",
            "sku", "gtin", 10.0, DimensionUnit.CM, 10.0, 10.0, 10.0,
            "metaTitle", "metaKeyword", "metaDescription", 1L,
            new ArrayList<>(List.of(1L)),
            List.of(),
            List.of(new ProductOptionValuePutVm(1L, "text", 1, List.of("Red"))),
            List.of(new ProductOptionValueDisplay(1L, "text", 1, "Red")),
            new ArrayList<>(List.of(2L)),
            1L
        );
        savedProduct = Product.builder().id(1L).name("Product 1").slug("slug").build();
    }

    @Test
    void createProduct_GivenValidData_ShouldSaveAndReturnProduct() {
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(new Category(), new Category()));
        when(productRepository.findAllById(anyList())).thenReturn(List.of(Product.builder().id(2L).slug("related").build()));

        var result = productService.createProduct(productPostVm);

        assertThat(result).isNotNull();
        assertThat(result.slug()).isEqualTo("slug");
        verify(productRepository).save(any(Product.class));
        verify(productCategoryRepository).saveAll(anyList());
        verify(productImageRepository).saveAll(anyList());
        verify(productRelatedRepository).saveAll(anyList());
    }

    @Test
    void createProduct_WithVariations_ShouldSaveVariationsAndCombinations() {
        ProductVariationPostVm varPost = new ProductVariationPostVm("Var1", "slug-var", "sku-var", "gtin-var", 15.0, null, List.of(), Map.of(1L, "Red"));
        ProductOptionValuePostVm optValPost = new ProductOptionValuePostVm(1L, "text", 1, List.of("Red"));
        ProductOptionValueDisplay optDisplay = new ProductOptionValueDisplay(1L, "text", 1, "Red");
        
        ProductPostVm vmWithVar = new ProductPostVm(
            "Product 1", "slug", 1L, List.of(), "shortDesc", "desc", "spec", "sku", "gtin", 10.0, DimensionUnit.CM, 10.0, 10.0, 10.0,
            10.0, true, true, true, true, true, "metaTitle", "metaKeyword", "metaDescription", 1L,
            List.of(), List.of(varPost), List.of(optValPost), List.of(optDisplay), List.of(), 1L
        );
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        
        Product variation = Product.builder().id(2L).slug("slug-var").build();
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct).thenReturn(savedProduct);
        when(productRepository.saveAll(anyList())).thenReturn(List.of(variation));
        
        ProductOption option = new ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));
        
        ProductOptionValue pov = new ProductOptionValue();
        pov.setProductOption(option);
        when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(pov));

        var result = productService.createProduct(vmWithVar);

        assertThat(result).isNotNull();
        verify(productOptionValueRepository).saveAll(anyList());
        verify(productOptionCombinationRepository).saveAll(anyList());
    }

    @Test
    void updateProduct_GivenValidData_ShouldUpdateSuccessfully() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.of(new Brand()));
        when(categoryRepository.findAllById(anyList())).thenReturn(List.of(new Category(), new Category()));
        ProductOption option = new ProductOption();
        option.setId(1L);
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of(option));
        ProductOptionValue updatedOptionValue = new ProductOptionValue();
        updatedOptionValue.setProductOption(option);
        when(productOptionValueRepository.saveAll(anyList())).thenReturn(List.of(updatedOptionValue));
        
        ProductRelated prodR = ProductRelated.builder().product(savedProduct).relatedProduct(Product.builder().id(3L).build()).build();
        savedProduct.setRelatedProducts(new ArrayList<>(List.of(prodR)));
        savedProduct.setProducts(new ArrayList<>());
        when(productRepository.findAllById(anyList())).thenReturn(List.of(Product.builder().id(2L).build()));

        productService.updateProduct(1L, productPutVm);

        verify(productRepository).findById(1L);
        verify(productCategoryRepository).deleteAllInBatch(anyList());
        verify(productCategoryRepository).saveAll(anyList());
        verify(productRelatedRepository).deleteAll(anyList());
        verify(productRelatedRepository).saveAll(anyList());
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void createProduct_LengthLessThanWidth_ThrowsBadRequestException() {
        ProductPostVm invalidVm = new ProductPostVm(
            "Product invalid", "slug-invalid", 1L, List.of(), "shortDesc", "desc", "spec",
            "sku-invalid", "gtin-invalid", 10.0, DimensionUnit.CM, 8.0, 10.0, 10.0,
            10.0, true, true, true, true, true,
            "metaTitle", "metaKeyword", "metaDescription", 1L,
            List.of(), List.of(), List.of(), List.of(), List.of(), 1L
        );

        assertThrows(BadRequestException.class, () -> productService.createProduct(invalidVm));
    }

    @Test
    void createProduct_BrandNotFound_ThrowsNotFoundException() {
        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(brandRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_DuplicateSlug_ThrowsDuplicatedException() {
        when(productRepository.findBySlugAndIsPublishedTrue(anyString()))
            .thenReturn(Optional.of(Product.builder().id(99L).slug("slug").build()));

        assertThrows(DuplicatedException.class, () -> productService.createProduct(productPostVm));
    }

    @Test
    void createProduct_WithVariationsAndNoMatchingOptions_ThrowsBadRequestException() {
        ProductVariationPostVm varPost = new ProductVariationPostVm(
            "Var1", "slug-var-2", "sku-var-2", "gtin-var-2", 15.0, null, List.of(), Map.of(1L, "Red"));
        ProductOptionValuePostVm optValPost = new ProductOptionValuePostVm(1L, "text", 1, List.of("Red"));
        ProductOptionValueDisplay optDisplay = new ProductOptionValueDisplay(1L, "text", 1, "Red");
        ProductPostVm vmWithVar = new ProductPostVm(
            "Product 2", "slug-2", null, List.of(), "shortDesc", "desc", "spec", "sku-2", "gtin-2",
            10.0, DimensionUnit.CM, 10.0, 10.0, 10.0, 10.0, true, true, true, true, true,
            "metaTitle", "metaKeyword", "metaDescription", 1L,
            List.of(), List.of(varPost), List.of(optValPost), List.of(optDisplay), List.of(), 1L
        );

        when(productRepository.findBySlugAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findByGtinAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.findBySkuAndIsPublishedTrue(anyString())).thenReturn(Optional.empty());
        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);
        when(productRepository.saveAll(anyList())).thenReturn(List.of(Product.builder().id(2L).slug("slug-var-2").build()));
        when(productOptionRepository.findAllByIdIn(anyList())).thenReturn(List.of());

        assertThrows(BadRequestException.class, () -> productService.createProduct(vmWithVar));
    }

    @Test
    void updateProduct_ProductNotFound_ThrowsNotFoundException() {
        when(productRepository.findById(123L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.updateProduct(123L, productPutVm));
    }

    @Test
    void updateProduct_DuplicateSlug_ThrowsDuplicatedException() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(savedProduct));
        when(productRepository.findBySlugAndIsPublishedTrue(anyString()))
            .thenReturn(Optional.of(Product.builder().id(2L).slug("slug").build()));

        assertThrows(DuplicatedException.class, () -> productService.updateProduct(1L, productPutVm));
    }
}
