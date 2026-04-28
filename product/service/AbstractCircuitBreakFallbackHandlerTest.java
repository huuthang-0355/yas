package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class AbstractCircuitBreakFallbackHandlerTest {

    private final TestFallbackHandler handler = new TestFallbackHandler();

    @Test
    void test_handle_bodiless_fallback_rethrows() {
        IllegalStateException error = new IllegalStateException("boom");

        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
            handler.callBodiless(error)
        );

        assertThat(thrown).isSameAs(error);
    }

    @Test
    void test_handle_typed_fallback_rethrows() {
        IllegalArgumentException error = new IllegalArgumentException("bad");

        IllegalArgumentException thrown = assertThrows(IllegalArgumentException.class, () ->
            handler.callTyped(error)
        );

        assertThat(thrown).isSameAs(error);
    }

    private static class TestFallbackHandler extends AbstractCircuitBreakFallbackHandler {
        void callBodiless(Throwable throwable) throws Throwable {
            handleBodilessFallback(throwable);
        }

        <T> T callTyped(Throwable throwable) throws Throwable {
            return handleTypedFallback(throwable);
        }
    }
}
