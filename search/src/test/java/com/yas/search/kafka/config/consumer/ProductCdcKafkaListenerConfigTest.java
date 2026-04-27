package com.yas.search.kafka.config.consumer;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.boot.kafka.autoconfigure.KafkaProperties;

class ProductCdcKafkaListenerConfigTest {

    @Test
    void testListenerContainerFactory_returnsConfiguredFactory() {
        KafkaProperties kafkaProperties = new KafkaProperties();
        kafkaProperties.getConsumer().setGroupId("search-test-group");

        ProductCdcKafkaListenerConfig config = new ProductCdcKafkaListenerConfig(kafkaProperties);

        var factory = config.listenerContainerFactory();

        assertThat(factory).isNotNull();
        assertThat(factory.getConsumerFactory()).isNotNull();
    }
}
