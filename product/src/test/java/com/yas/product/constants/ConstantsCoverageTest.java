package com.yas.product.constants;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ConstantsCoverageTest {

    @Test
    void testPageableConstant() {
        PageableConstant constant = new PageableConstant();
        assertNotNull(constant);
        assertEquals("10", PageableConstant.DEFAULT_PAGE_SIZE);
        assertEquals("0", PageableConstant.DEFAULT_PAGE_NUMBER);
    }
}
