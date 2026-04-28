package com.yas.product.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void test_get_message_formats_parameters() {
        String result = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", 123);

        assertThat(result).isEqualTo("Product 123 is not found");
    }

    @Test
    void test_get_message_returns_code_when_missing() {
        String result = MessagesUtils.getMessage("UNKNOWN_CODE");

        assertThat(result).isEqualTo("UNKNOWN_CODE");
    }
}
