package com.yas.product;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class ProductApplicationTest {

    @Test
    void testProductApplicationInstantiation() {
        ProductApplication app = new ProductApplication();
        assertNotNull(app);
    }
}
