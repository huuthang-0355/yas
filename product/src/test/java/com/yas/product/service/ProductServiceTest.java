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
import com.yas.product.model.ProductImage;
import com.yas.product.model.ProductOption;
import com.yas.product.model.ProductOptionCombination;
import com.yas.product.model.ProductOptionValue;
import com.yas.product.model.ProductRelated;
import com.yas.product.model.ProductCategory;
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
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.NoFileMediaVm;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
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

    @Test
    void getLatestProducts_CountIsZero_ReturnsEmptyList() {
        assertThat(productService.getLatestProducts(0)).isEmpty();
    }

    @Test
    void getLatestProducts_WithProducts_ReturnsMappedProducts() {
        Product p = Product.builder().id(11L).name("P11").slug("p11").price(12.0).build();
        when(productRepository.getLatestProducts(any())).thenReturn(List.of(p));

        assertThat(productService.getLatestProducts(1)).hasSize(1);
    }

    @Test
    void getProductSlug_WithParent_ReturnsParentSlug() {
        Product parent = Product.builder().id(100L).slug("parent-slug").build();
        Product child = Product.builder().id(101L).slug("child-slug").parent(parent).build();
        when(productRepository.findById(101L)).thenReturn(Optional.of(child));

        var result = productService.getProductSlug(101L);

        assertThat(result.slug()).isEqualTo("parent-slug");
        assertThat(result.productVariantId()).isEqualTo(101L);
    }

    @Test
    void deleteProduct_WhenChildHasCombinations_ShouldDeleteCombinationsAndSave() {
        Product parent = Product.builder().id(10L).build();
        Product child = Product.builder().id(20L).parent(parent).build();
        ProductOptionCombination poc = ProductOptionCombination.builder().id(1L).product(child).build();
        when(productRepository.findById(20L)).thenReturn(Optional.of(child));
        when(productOptionCombinationRepository.findAllByProduct(child)).thenReturn(List.of(poc));

        productService.deleteProduct(20L);

        verify(productOptionCombinationRepository).deleteAll(anyList());
        verify(productRepository).save(child);
    }

    @Test
    void getFeaturedProductsById_UsesParentThumbnailWhenChildThumbnailIsEmpty() {
        Product parent = Product.builder().id(30L).thumbnailMediaId(300L).build();
        Product child = Product.builder().id(31L).name("Child").slug("child").price(20.0).parent(parent).thumbnailMediaId(301L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(child));
        when(productRepository.findById(30L)).thenReturn(Optional.of(parent));
        when(mediaService.getMedia(301L)).thenReturn(new NoFileMediaVm(301L, "", "", "", ""));
        when(mediaService.getMedia(300L)).thenReturn(new NoFileMediaVm(300L, "", "", "", "http://parent-thumb"));

        var result = productService.getFeaturedProductsById(List.of(31L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().thumbnailUrl()).isEqualTo("http://parent-thumb");
    }

    @Test
    void getProductVariationsByParentId_HasNoOptions_ReturnsEmptyList() {
        Product parent = Product.builder().id(40L).hasOptions(false).build();
        when(productRepository.findById(40L)).thenReturn(Optional.of(parent));

        assertThat(productService.getProductVariationsByParentId(40L)).isEmpty();
    }

    @Test
    void getProductVariationsByParentId_HasOptions_ReturnsMappedVariations() {
        ProductOption option = new ProductOption();
        option.setId(77L);
        Product variation = Product.builder().id(51L).name("Var").slug("var").sku("sku-v").gtin("gtin-v").price(9.9).isPublished(true)
            .thumbnailMediaId(510L).productImages(List.of(ProductImage.builder().imageId(511L).build())).build();
        Product parent = Product.builder().id(50L).hasOptions(true).products(List.of(variation)).build();
        ProductOptionCombination combination = ProductOptionCombination.builder()
            .id(9L).product(variation).productOption(option).value("Red").displayOrder(1).build();

        when(productRepository.findById(50L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of(combination));
        when(mediaService.getMedia(510L)).thenReturn(new NoFileMediaVm(510L, "", "", "", "http://thumb"));
        when(mediaService.getMedia(511L)).thenReturn(new NoFileMediaVm(511L, "", "", "", "http://img"));

        var result = productService.getProductVariationsByParentId(50L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().options()).containsEntry(77L, "Red");
    }

    @Test
    void subtractStockQuantity_ShouldNotGoBelowZero() {
        Product p = Product.builder().id(60L).stockTrackingEnabled(true).stockQuantity(3L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));

        productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(60L, 5L)));

        assertThat(p.getStockQuantity()).isEqualTo(0L);
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void restoreStockQuantity_ShouldAddQuantities() {
        Product p = Product.builder().id(70L).stockTrackingEnabled(true).stockQuantity(10L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));

        productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(70L, 4L), new ProductQuantityPutVm(70L, 6L)));

        assertThat(p.getStockQuantity()).isEqualTo(20L);
        verify(productRepository).saveAll(anyList());
    }

    @Test
    void getLatestProducts_WhenRepositoryReturnsEmpty_ReturnsEmptyList() {
        when(productRepository.getLatestProducts(any())).thenReturn(List.of());

        assertThat(productService.getLatestProducts(5)).isEmpty();
    }

    @Test
    void getProductSlug_WithoutParent_ReturnsOwnSlugAndNullVariantId() {
        Product product = Product.builder().id(81L).slug("own-slug").build();
        when(productRepository.findById(81L)).thenReturn(Optional.of(product));

        var result = productService.getProductSlug(81L);

        assertThat(result.slug()).isEqualTo("own-slug");
        assertThat(result.productVariantId()).isNull();
    }

    @Test
    void getProductByIds_ReturnsMappedList() {
        Product p = Product.builder().id(82L).name("P82").slug("p82").price(5.5).build();
        when(productRepository.findAllByIdIn(List.of(82L))).thenReturn(List.of(p));

        var result = productService.getProductByIds(List.of(82L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(82L);
    }

    @Test
    void getProductByCategoryIds_ReturnsMappedList() {
        Product p = Product.builder().id(83L).name("P83").slug("p83").price(6.5).build();
        when(productRepository.findByCategoryIdsIn(List.of(1L))).thenReturn(List.of(p));

        var result = productService.getProductByCategoryIds(List.of(1L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(83L);
    }

    @Test
    void getProductByBrandIds_ReturnsMappedList() {
        Product p = Product.builder().id(84L).name("P84").slug("p84").price(7.5).build();
        when(productRepository.findByBrandIdsIn(List.of(2L))).thenReturn(List.of(p));

        var result = productService.getProductByBrandIds(List.of(2L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(84L);
    }

    @Test
    void deleteProduct_WhenMainProduct_ShouldMarkUnpublishedAndSaveOnly() {
        Product main = Product.builder().id(85L).build();
        when(productRepository.findById(85L)).thenReturn(Optional.of(main));

        productService.deleteProduct(85L);

        assertThat(main.isPublished()).isFalse();
        verify(productRepository).save(main);
    }

    @Test
    void getProductsByBrand_BrandNotFound_ThrowsNotFoundException() {
        when(brandRepository.findBySlug("missing-brand")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsByBrand("missing-brand"));
    }

    @Test
    void getProductsByBrand_Success_ReturnsThumbnailVm() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setSlug("nike");
        Product p = Product.builder().id(86L).name("Shoe").slug("shoe").thumbnailMediaId(860L).build();

        when(brandRepository.findBySlug("nike")).thenReturn(Optional.of(brand));
        when(productRepository.findAllByBrandAndIsPublishedTrueOrderByIdAsc(brand)).thenReturn(List.of(p));
        when(mediaService.getMedia(860L)).thenReturn(new NoFileMediaVm(860L, "", "", "", "http://img-860"));

        var result = productService.getProductsByBrand("nike");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().slug()).isEqualTo("shoe");
    }

    @Test
    void getProductById_NotFound_ThrowsNotFoundException() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductById(999L));
    }

    @Test
    void getProductById_WithImagesAndBrand_ReturnsDetailVm() {
        Brand brand = new Brand();
        brand.setId(11L);

        Category category = new Category();
        category.setId(12L);

        ProductCategory productCategory = ProductCategory.builder().category(category).build();
        Product product = Product.builder()
            .id(87L)
            .name("P87")
            .slug("p87")
            .brand(brand)
            .thumbnailMediaId(870L)
            .productImages(List.of(ProductImage.builder().imageId(871L).build()))
            .productCategories(List.of(productCategory))
            .build();

        when(productRepository.findById(87L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(870L)).thenReturn(new NoFileMediaVm(870L, "", "", "", "http://thumb-870"));
        when(mediaService.getMedia(871L)).thenReturn(new NoFileMediaVm(871L, "", "", "", "http://img-871"));

        var result = productService.getProductById(87L);

        assertThat(result.id()).isEqualTo(87L);
        assertThat(result.brandId()).isEqualTo(11L);
        assertThat(result.productImageMedias()).hasSize(1);
    }

    @Test
    void getProductCheckoutList_WithThumbnailAndWithoutThumbnail() {
        Brand brand = new Brand();
        brand.setId(10L);

        Product p1 = Product.builder().id(88L).name("P88").slug("p88").brand(brand).thumbnailMediaId(880L).price(20.0).build();
        Product p2 = Product.builder().id(89L).name("P89").slug("p89").brand(brand).thumbnailMediaId(890L).price(30.0).build();
        Page<Product> page = new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 10), 2);

        when(productRepository.findAllPublishedProductsByIds(anyList(), any())).thenReturn(page);
        when(mediaService.getMedia(880L)).thenReturn(new NoFileMediaVm(880L, "", "", "", "http://thumb-880"));
        when(mediaService.getMedia(890L)).thenReturn(new NoFileMediaVm(890L, "", "", "", ""));

        var result = productService.getProductCheckoutList(0, 10, List.of(88L, 89L));

        assertThat(result.productCheckoutListVms()).hasSize(2);
        assertThat(result.productCheckoutListVms().getFirst().thumbnailUrl()).isEqualTo("http://thumb-880");
        assertThat(result.productCheckoutListVms().get(1).thumbnailUrl()).isNullOrEmpty();
    }
}
