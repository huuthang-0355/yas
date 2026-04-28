package test.java.com.yas.product.utils;

import static org.assertj.core.api.Assertions.assertThat;

import com.yas.product.utils.ProductConverter;
import org.junit.jupiter.api.Test;

class ProductConverterTest {

    @Test
    void test_to_slug_trims_lowercases_and_replaces() {
        String result = ProductConverter.toSlug("  Hello World!  ");

        assertThat(result).isEqualTo("hello-world-");
    }

    @Test
    void test_to_slug_collapses_dashes_and_removes_leading() {
        String result = ProductConverter.toSlug("--Hello  World");

        assertThat(result).isEqualTo("hello-world");
    }
}
