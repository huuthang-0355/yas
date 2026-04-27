package com.yas.search.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MessagesUtilsTest {

    @Test
    void testGetMessage_whenCodeExists_formatsTemplate() {
        String message = MessagesUtils.getMessage("PRODUCT_NOT_FOUND", 100L);

        assertThat(message).isEqualTo("The product 100 is not found");
    }

    @Test
    void testGetMessage_whenCodeDoesNotExist_returnsCode() {
        String message = MessagesUtils.getMessage("UNKNOWN_CODE", "x");

        assertThat(message).isEqualTo("UNKNOWN_CODE");
    }
}
