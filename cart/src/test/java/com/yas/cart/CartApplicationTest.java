package com.yas.cart;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class CartApplicationTest {

    @Test
    void testCartApplicationInstantiation() {
        CartApplication app = new CartApplication();
        assertNotNull(app);
    }
}
