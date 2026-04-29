package com.yas.cart.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ConstantsCoverageTest {

    @Test
    void testConstants() {
        Constants constants = new Constants();
        assertNotNull(constants);
        assertEquals("NOT_FOUND_PRODUCT", Constants.ErrorCode.NOT_FOUND_PRODUCT);
    }
}
