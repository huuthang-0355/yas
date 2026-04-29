package com.yas.product.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class UtilsCoverageTest {

    @Test
    void testConstants() {
        Constants constants = new Constants();
        Constants.ErrorCode errorCode = constants.new ErrorCode();
        
        assertNotNull(constants);
        assertNotNull(errorCode);
        assertEquals("PRODUCT_NOT_FOUND", Constants.ErrorCode.PRODUCT_NOT_FOUND);
    }

    @Test
    void testMessagesUtils() {
        MessagesUtils utils = new MessagesUtils();
        assertNotNull(utils);
        
        String result = MessagesUtils.getMessage("nonexistent.error.code");
        assertEquals("nonexistent.error.code", result);
    }

    @Test
    void testProductConverter() {
        ProductConverter converter = new ProductConverter();
        assertNotNull(converter);
        
        String slug1 = ProductConverter.toSlug("  My Product! 123 ");
        assertEquals("my-product-123", slug1);
        
        String slug2 = ProductConverter.toSlug("---test---");
        assertEquals("test-", slug2);
    }
}
