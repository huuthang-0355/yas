package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
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
import com.yas.product.model.attribute.ProductAttribute;
import com.yas.product.model.attribute.ProductAttributeGroup;
import com.yas.product.model.attribute.ProductAttributeValue;
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
import com.yas.product.viewmodel.product.ProductDetailGetVm;
import com.yas.product.viewmodel.product.ProductEsDetailVm;
import com.yas.product.viewmodel.product.ProductFeatureGetVm;
import com.yas.product.viewmodel.product.ProductInfoVm;
import com.yas.product.viewmodel.product.ProductListGetFromCategoryVm;
import com.yas.product.viewmodel.product.ProductListGetVm;
import com.yas.product.viewmodel.product.ProductPostVm;
import com.yas.product.viewmodel.product.ProductPutVm;
import com.yas.product.viewmodel.product.ProductQuantityPostVm;
import com.yas.product.viewmodel.product.ProductQuantityPutVm;
import com.yas.product.viewmodel.product.ProductVariationPostVm;
import com.yas.product.viewmodel.product.ProductsGetVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePostVm;
import com.yas.product.viewmodel.productoption.ProductOptionValuePutVm;
import com.yas.product.viewmodel.product.ProductOptionValueDisplay;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.model.enumeration.FilterExistInWhSelection;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.ArrayList;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
    void getFeaturedProductsById_UsesOwnThumbnailWhenPresent() {
        Product product = Product.builder().id(32L).name("P32").slug("p32").price(5.0).thumbnailMediaId(320L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(product));
        when(mediaService.getMedia(320L)).thenReturn(new NoFileMediaVm(320L, "", "", "", "http://thumb-320"));

        var result = productService.getFeaturedProductsById(List.of(32L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().thumbnailUrl()).isEqualTo("http://thumb-320");
    }

    @Test
    void getFeaturedProductsById_WhenParentMissing_UsesEmptyString() {
        Product parent = Product.builder().id(33L).build();
        Product child = Product.builder().id(34L).name("Child").slug("child").price(6.0)
            .parent(parent).thumbnailMediaId(340L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(child));
        when(productRepository.findById(33L)).thenReturn(Optional.empty());
        when(mediaService.getMedia(340L)).thenReturn(new NoFileMediaVm(340L, "", "", "", ""));

        var result = productService.getFeaturedProductsById(List.of(34L));

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().thumbnailUrl()).isEmpty();
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
    void getProductVariationsByParentId_HasOptionsWithoutThumbnail_ReturnsNullImage() {
        Product variation = Product.builder().id(52L).name("Var2").slug("var2").price(10.0)
            .isPublished(true).thumbnailMediaId(null).productImages(List.of()).build();
        Product parent = Product.builder().id(52L).hasOptions(true).products(List.of(variation)).build();

        when(productRepository.findById(52L)).thenReturn(Optional.of(parent));
        when(productOptionCombinationRepository.findAllByProduct(variation)).thenReturn(List.of());

        var result = productService.getProductVariationsByParentId(52L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().image()).isNull();
        assertThat(result.getFirst().options()).isEmpty();
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
    void subtractStockQuantity_SkipsWhenTrackingDisabled() {
        Product p = Product.builder().id(71L).stockTrackingEnabled(false).stockQuantity(8L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));

        productService.subtractStockQuantity(List.of(new ProductQuantityPutVm(71L, 5L)));

        assertThat(p.getStockQuantity()).isEqualTo(8L);
    }

    @Test
    void restoreStockQuantity_SkipsWhenTrackingDisabled() {
        Product p = Product.builder().id(72L).stockTrackingEnabled(false).stockQuantity(8L).build();
        when(productRepository.findAllByIdIn(anyList())).thenReturn(List.of(p));

        productService.restoreStockQuantity(List.of(new ProductQuantityPutVm(72L, 5L)));

        assertThat(p.getStockQuantity()).isEqualTo(8L);
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
    void getProductById_WithNullCollections_ReturnsEmptyLists() {
        Product product = Product.builder().id(870L).name("P870").slug("p870").build();
        product.setProductImages(null);
        product.setProductCategories(null);

        when(productRepository.findById(870L)).thenReturn(Optional.of(product));

        var result = productService.getProductById(870L);

        assertThat(result.productImageMedias()).isEmpty();
        assertThat(result.categories()).isEmpty();
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

    @Test
    void getProductsWithFilter_TrimsAndMapsResults() {
        Product product = Product.builder().id(90L).name("P90").slug("p90").price(9.0).build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 2), 1);
        when(productRepository.getProductsWithFilter(eq("name"), eq("Brand"), any(Pageable.class)))
            .thenReturn(page);

        ProductListGetVm result = productService.getProductsWithFilter(0, 2, "  Name ", "Brand ");

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.totalElements()).isEqualTo(1);
        verify(productRepository).getProductsWithFilter(eq("name"), eq("Brand"), any(Pageable.class));
    }

    @Test
    void getProductsFromCategory_CategoryNotFound_ThrowsNotFoundException() {
        when(categoryRepository.findBySlug("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductsFromCategory(0, 5, "missing"));
    }

    @Test
    void getProductsFromCategory_ReturnsThumbnailList() {
        Category category = new Category();
        category.setId(1L);
        category.setSlug("cat");
        category.setName("Category");

        Product product = Product.builder().id(91L).name("P91").slug("p91").thumbnailMediaId(910L).build();
        ProductCategory productCategory = ProductCategory.builder().product(product).category(category).build();
        Page<ProductCategory> page = new PageImpl<>(List.of(productCategory), PageRequest.of(0, 2), 1);

        when(categoryRepository.findBySlug("cat")).thenReturn(Optional.of(category));
        when(productCategoryRepository.findAllByCategory(any(Pageable.class), eq(category))).thenReturn(page);
        when(mediaService.getMedia(910L)).thenReturn(new NoFileMediaVm(910L, "", "", "", "http://img-910"));

        ProductListGetFromCategoryVm result = productService.getProductsFromCategory(0, 2, "cat");

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().getFirst().thumbnailUrl()).isEqualTo("http://img-910");
    }

    @Test
    void getListFeaturedProducts_ReturnsListAndTotalPages() {
        Product p1 = Product.builder().id(92L).name("P92").slug("p92").thumbnailMediaId(920L).price(11.0).build();
        Product p2 = Product.builder().id(93L).name("P93").slug("p93").thumbnailMediaId(930L).price(12.0).build();
        Page<Product> page = new PageImpl<>(List.of(p1, p2), PageRequest.of(0, 2), 3);

        when(productRepository.getFeaturedProduct(any(Pageable.class))).thenReturn(page);
        when(mediaService.getMedia(920L)).thenReturn(new NoFileMediaVm(920L, "", "", "", "http://img-920"));
        when(mediaService.getMedia(930L)).thenReturn(new NoFileMediaVm(930L, "", "", "", "http://img-930"));

        ProductFeatureGetVm result = productService.getListFeaturedProducts(0, 2);

        assertThat(result.productList()).hasSize(2);
        assertThat(result.totalPage()).isEqualTo(2);
    }

    @Test
    void getProductDetail_MapsAttributesAndImages() {
        Brand brand = new Brand();
        brand.setName("Brand");

        Category category = new Category();
        category.setName("Category");

        ProductAttributeGroup group = new ProductAttributeGroup();
        group.setId(1L);
        group.setName("Group A");

        ProductAttribute attr1 = ProductAttribute.builder().id(1L).name("Color").productAttributeGroup(group).build();
        ProductAttribute attr2 = ProductAttribute.builder().id(2L).name("Size").productAttributeGroup(null).build();

        ProductAttributeValue val1 = new ProductAttributeValue();
        val1.setProductAttribute(attr1);
        val1.setValue("Red");
        ProductAttributeValue val2 = new ProductAttributeValue();
        val2.setProductAttribute(attr2);
        val2.setValue("M");

        Product product = Product.builder()
            .id(94L)
            .name("P94")
            .slug("p94")
            .brand(brand)
            .thumbnailMediaId(940L)
            .productImages(List.of(ProductImage.builder().imageId(941L).build()))
            .productCategories(List.of(ProductCategory.builder().category(category).build()))
            .attributeValues(List.of(val1, val2))
            .build();

        when(productRepository.findBySlugAndIsPublishedTrue("p94")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(940L)).thenReturn(new NoFileMediaVm(940L, "", "", "", "http://thumb-940"));
        when(mediaService.getMedia(941L)).thenReturn(new NoFileMediaVm(941L, "", "", "", "http://img-941"));

        ProductDetailGetVm result = productService.getProductDetail("p94");

        assertThat(result.productCategories()).containsExactly("Category");
        assertThat(result.productImageMediaUrls()).containsExactly("http://img-941");
        assertThat(result.productAttributeGroups()).hasSize(2);
        assertThat(result.productAttributeGroups().stream().map(groupVm -> groupVm.name()))
            .containsExactlyInAnyOrder("Group A", "None group");
    }

    @Test
    void getProductDetail_WhenNotFound_ThrowsNotFoundException() {
        when(productRepository.findBySlugAndIsPublishedTrue("missing")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productService.getProductDetail("missing"));
    }

    @Test
    void getProductDetail_WhenNoImagesOrAttributes_ReturnsEmptyCollections() {
        Product product = Product.builder().id(941L).name("P941").slug("p941").thumbnailMediaId(9410L)
            .productImages(List.of()).productCategories(List.of()).attributeValues(List.of()).build();

        when(productRepository.findBySlugAndIsPublishedTrue("p941")).thenReturn(Optional.of(product));
        when(mediaService.getMedia(9410L)).thenReturn(new NoFileMediaVm(9410L, "", "", "", "http://thumb-9410"));

        ProductDetailGetVm result = productService.getProductDetail("p941");

        assertThat(result.productCategories()).isEmpty();
        assertThat(result.productImageMediaUrls()).isEmpty();
        assertThat(result.productAttributeGroups()).isEmpty();
    }

    @Test
    void getProductsByMultiQuery_UsesFiltersAndMapsResults() {
        Product product = Product.builder().id(95L).name("P95").slug("p95").thumbnailMediaId(950L).price(15.0).build();
        Page<Product> page = new PageImpl<>(List.of(product), PageRequest.of(0, 1), 1);

        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
            eq("name"), eq("cat"), eq(10.0), eq(20.0), any(Pageable.class)))
            .thenReturn(page);
        when(mediaService.getMedia(950L)).thenReturn(new NoFileMediaVm(950L, "", "", "", "http://img-950"));

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 1, " Name ", "cat ", 10.0, 20.0);

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().getFirst().thumbnailUrl()).isEqualTo("http://img-950");
    }

    @Test
    void getProductsByMultiQuery_WhenEmpty_ReturnsEmptyList() {
        when(productRepository.findByProductNameAndCategorySlugAndPriceBetween(
            eq("name"), eq(""), isNull(), isNull(), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(), PageRequest.of(0, 1), 0));

        ProductsGetVm result = productService.getProductsByMultiQuery(0, 1, " Name ", " ", null, null);

        assertThat(result.productContent()).isEmpty();
    }

    @Test
    void getRelatedProductsBackoffice_ReturnsMappedList() {
        Product related = Product.builder().id(96L).name("Related").slug("rel").price(5.0)
            .createdOn(ZonedDateTime.now()).taxClassId(1L).build();
        Product main = Product.builder().id(97L).build();
        ProductRelated relation = ProductRelated.builder().id(1L).product(main).relatedProduct(related).build();
        main.setRelatedProducts(List.of(relation));

        when(productRepository.findById(97L)).thenReturn(Optional.of(main));

        var result = productService.getRelatedProductsBackoffice(97L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().id()).isEqualTo(96L);
    }

    @Test
    void getRelatedProductsStorefront_FiltersUnpublished() {
        Product main = Product.builder().id(98L).build();
        Product published = Product.builder().id(99L).name("Pub").slug("pub").price(10.0)
            .thumbnailMediaId(990L).isPublished(true).build();
        Product hidden = Product.builder().id(100L).name("Hidden").slug("hidden").price(11.0)
            .thumbnailMediaId(1000L).isPublished(false).build();
        ProductRelated rel1 = ProductRelated.builder().product(main).relatedProduct(published).build();
        ProductRelated rel2 = ProductRelated.builder().product(main).relatedProduct(hidden).build();

        when(productRepository.findById(98L)).thenReturn(Optional.of(main));
        when(productRelatedRepository.findAllByProduct(eq(main), any(Pageable.class)))
            .thenReturn(new PageImpl<>(List.of(rel1, rel2), PageRequest.of(0, 2), 2));
        when(mediaService.getMedia(990L)).thenReturn(new NoFileMediaVm(990L, "", "", "", "http://img-990"));

        ProductsGetVm result = productService.getRelatedProductsStorefront(98L, 0, 2);

        assertThat(result.productContent()).hasSize(1);
        assertThat(result.productContent().getFirst().id()).isEqualTo(99L);
    }

    @Test
    void exportProducts_ReturnsMappedResults() {
        Brand brand = new Brand();
        brand.setId(5L);
        brand.setName("BrandX");
        Product product = Product.builder().id(101L).name("P101").slug("p101").brand(brand).price(30.0).build();

        when(productRepository.getExportingProducts(eq("name"), eq("brand")))
            .thenReturn(List.of(product));

        var result = productService.exportProducts(" Name ", "Brand ");

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().brandName()).isEqualTo("BrandX");
    }

    @Test
    void getProductsForWarehouse_MapsResults() {
        Product product = Product.builder().id(105L).name("P105").sku("sku-105").build();
        when(productRepository.findProductForWarehouse("name", "sku", List.of(105L), FilterExistInWhSelection.ALL.name()))
            .thenReturn(List.of(product));

        List<ProductInfoVm> result = productService.getProductsForWarehouse("name", "sku", List.of(105L), FilterExistInWhSelection.ALL);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().sku()).isEqualTo("sku-105");
    }

    @Test
    void getProductEsDetailById_WithNullBrandAndThumbnail_ReturnsNulls() {
        Product product = Product.builder().id(106L).name("P106").slug("p106").build();

        when(productRepository.findById(106L)).thenReturn(Optional.of(product));

        ProductEsDetailVm result = productService.getProductEsDetailById(106L);

        assertThat(result.brand()).isNull();
        assertThat(result.thumbnailMediaId()).isNull();
    }

    @Test
    void getProductEsDetailById_ReturnsCategoryAndAttributeNames() {
        Brand brand = new Brand();
        brand.setName("BrandY");

        Category category = new Category();
        category.setName("CatY");

        ProductAttribute attribute = ProductAttribute.builder().id(3L).name("Material").build();
        ProductAttributeValue value = new ProductAttributeValue();
        value.setProductAttribute(attribute);

        Product product = Product.builder()
            .id(102L)
            .name("P102")
            .slug("p102")
            .brand(brand)
            .productCategories(List.of(ProductCategory.builder().category(category).build()))
            .attributeValues(List.of(value))
            .build();

        when(productRepository.findById(102L)).thenReturn(Optional.of(product));

        ProductEsDetailVm result = productService.getProductEsDetailById(102L);

        assertThat(result.brand()).isEqualTo("BrandY");
        assertThat(result.categories()).containsExactly("CatY");
        assertThat(result.attributes()).containsExactly("Material");
    }

    @Test
    void updateProductQuantity_UpdatesStockQuantities() {
        Product p1 = Product.builder().id(103L).stockQuantity(5L).build();
        Product p2 = Product.builder().id(104L).stockQuantity(7L).build();

        when(productRepository.findAllByIdIn(List.of(103L, 104L))).thenReturn(List.of(p1, p2));

        productService.updateProductQuantity(List.of(
            new ProductQuantityPostVm(103L, 12L),
            new ProductQuantityPostVm(104L, 20L)
        ));

        assertThat(p1.getStockQuantity()).isEqualTo(12L);
        assertThat(p2.getStockQuantity()).isEqualTo(20L);
        verify(productRepository).saveAll(anyList());
    }
}
