package com.yas.search.viewmodel.error;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ErrorVmTest {

    @Test
    void testThreeArgsConstructor_initializesEmptyFieldErrors() {
        ErrorVm vm = new ErrorVm("400", "Bad Request", "Invalid payload");

        assertThat(vm.statusCode()).isEqualTo("400");
        assertThat(vm.title()).isEqualTo("Bad Request");
        assertThat(vm.detail()).isEqualTo("Invalid payload");
        assertThat(vm.fieldErrors()).isEmpty();
    }

    @Test
    void testCanonicalConstructor_setsProvidedFieldErrors() {
        ErrorVm vm = new ErrorVm("422", "Validation Error", "Has errors", List.of("name is required"));

        assertThat(vm.statusCode()).isEqualTo("422");
        assertThat(vm.title()).isEqualTo("Validation Error");
        assertThat(vm.detail()).isEqualTo("Has errors");
        assertThat(vm.fieldErrors()).containsExactly("name is required");
    }
}
