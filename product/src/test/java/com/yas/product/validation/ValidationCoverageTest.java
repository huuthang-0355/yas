package com.yas.product.validation;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ValidationCoverageTest {

    @Test
    void testPriceValidator() {
        PriceValidator validator = new PriceValidator();
        validator.initialize(null);
        
        assertTrue(validator.isValid(10.0, null));
        assertTrue(validator.isValid(0.0, null));
        assertFalse(validator.isValid(-5.0, null));
    }
}
