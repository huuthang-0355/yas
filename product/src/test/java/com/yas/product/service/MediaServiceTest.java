package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

@ExtendWith(MockitoExtension.class)
class MediaServiceTest {

    @Mock
    private RestClient restClient;

    @Mock
    private ServiceUrlConfig serviceUrlConfig;

    @InjectMocks
    private MediaService mediaService;

    @BeforeEach
    void setUp() {
    }

    @Test
    void getMedia_WhenIdIsNull_ShouldReturnEmptyMediaVm() {
        NoFileMediaVm result = mediaService.getMedia(null);
        assertThat(result.id()).isNull();
        assertThat(result.caption()).isEmpty();
    }
}
