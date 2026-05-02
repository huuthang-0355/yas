package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

class StringUtilsTest {

    @Test
    void hasText_whenNull_returnsFalse() {
        assertFalse(StringUtils.hasText(null));
    }

    @Test
    void hasText_whenEmpty_returnsFalse() {
        assertFalse(StringUtils.hasText(""));
    }

    @Test
    void hasText_whenBlank_returnsFalse() {
        assertFalse(StringUtils.hasText("   "));
    }

    @Test
    void hasText_whenHasText_returnsTrue() {
        assertTrue(StringUtils.hasText(" test "));
    }
}
