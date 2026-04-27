package com.yas.search.constant;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import org.junit.jupiter.api.Test;

class ConstantClassTest {

    @Test
    void testActionConstants_haveExpectedValues() {
        assertThat(Action.CREATE).isEqualTo("c");
        assertThat(Action.READ).isEqualTo("r");
        assertThat(Action.UPDATE).isEqualTo("u");
        assertThat(Action.DELETE).isEqualTo("d");
    }

    @Test
    void testMessageCodeConstant_haveExpectedValue() {
        assertThat(MessageCode.PRODUCT_NOT_FOUND).isEqualTo("PRODUCT_NOT_FOUND");
    }

    @Test
    void testProductFieldConstants_haveExpectedValues() {
        assertThat(ProductField.NAME).isEqualTo("name");
        assertThat(ProductField.BRAND).isEqualTo("brand");
        assertThat(ProductField.PRICE).isEqualTo("price");
        assertThat(ProductField.IS_PUBLISHED).isEqualTo("isPublished");
        assertThat(ProductField.CATEGORIES).isEqualTo("categories");
        assertThat(ProductField.ATTRIBUTES).isEqualTo("attributes");
        assertThat(ProductField.CREATE_ON).isEqualTo("createdOn");
    }

    @Test
    void testPrivateConstructors_actionAndMessageCode_areAccessibleViaReflection() throws Exception {
        Constructor<Action> actionConstructor = Action.class.getDeclaredConstructor();
        actionConstructor.setAccessible(true);
        assertThat(actionConstructor.newInstance()).isNotNull();

        Constructor<MessageCode> messageCodeConstructor = MessageCode.class.getDeclaredConstructor();
        messageCodeConstructor.setAccessible(true);
        assertThat(messageCodeConstructor.newInstance()).isNotNull();
    }

    @Test
    void testPrivateConstructor_productField_throwsUnsupportedOperationException() throws Exception {
        Constructor<ProductField> constructor = ProductField.class.getDeclaredConstructor();
        constructor.setAccessible(true);

        assertThatThrownBy(constructor::newInstance)
            .isInstanceOf(InvocationTargetException.class)
            .hasCauseInstanceOf(UnsupportedOperationException.class)
            .satisfies(ex -> assertThat(ex.getCause())
                .hasMessage("This is a utility class and cannot be instantiated"));
    }
}
