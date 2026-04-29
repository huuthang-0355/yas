package com.yas.product.validation;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PriceValidatorTest {

    private final PriceValidator validator = new PriceValidator();

    @Test
    void test_is_valid_allows_zero_and_positive_values() {
        assertThat(validator.isValid(0.0, null)).isTrue();
        assertThat(validator.isValid(10.5, null)).isTrue();
    }

    @Test
    void test_is_valid_rejects_negative_values() {
        assertThat(validator.isValid(-0.1, null)).isFalse();
    }
}
