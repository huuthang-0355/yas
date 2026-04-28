package com.yas.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.yas.commonlibrary.exception.NotFoundException;
import com.yas.product.model.Brand;
import com.yas.product.model.Product;
import com.yas.product.repository.ProductOptionCombinationRepository;
import com.yas.product.repository.ProductRepository;
import com.yas.product.viewmodel.NoFileMediaVm;
import com.yas.product.viewmodel.product.ProductDetailInfoVm;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ProductDetailServiceTest {

    @Mock
    private ProductRepository productRepository;

    @Mock
    private MediaService mediaService;

    @Mock
    private ProductOptionCombinationRepository productOptionCombinationRepository;

    @InjectMocks
    private ProductDetailService productDetailService;

    private Product product;
    
    @BeforeEach
    void setUp() {
        Brand brand = new Brand();
        brand.setId(1L);
        brand.setName("Test Brand");

        product = new Product();
        product.setId(1L);
        product.setName("Test Product");
        product.setPublished(true);
        product.setBrand(brand);
        product.setHasOptions(false);
        product.setThumbnailMediaId(10L);
    }

    @Test
    void getProductDetailById_WhenProductExists_ShouldReturnProductDetail() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "/url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Test Product", result.getName());
        assertEquals("Test Brand", result.getBrandName());
    }

    @Test
    void getProductDetailById_WhenProductNotPublished_ShouldThrowNotFoundException() {
        product.setPublished(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_WhenProductNotFound_ShouldThrowNotFoundException() {
        when(productRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> productDetailService.getProductDetailById(1L));
    }

    @Test
    void getProductDetailById_WhenHasOptions_ShouldReturnProductDetailWithVariations() {
        product.setHasOptions(true);
        
        Product childProduct = new Product();
        childProduct.setId(2L);
        childProduct.setName("Child Product");
        childProduct.setPublished(true);
        
        product.setProducts(List.of(childProduct));

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(mediaService.getMedia(10L)).thenReturn(new NoFileMediaVm(10L, "", "", "", "/url"));

        ProductDetailInfoVm result = productDetailService.getProductDetailById(1L);

        assertNotNull(result);
        assertEquals(1, result.getVariations().size());
        assertEquals(2L, result.getVariations().get(0).id());
    }
}
