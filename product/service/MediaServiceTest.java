package com.yas.product.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.any;

import com.yas.commonlibrary.config.ServiceUrlConfig;
import com.yas.product.viewmodel.NoFileMediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @Test
    void getMedia_WhenIdIsNotNull_ShouldReturnMediaVm() {
        Long mediaId = 1L;
        String mediaUrl = "http://media-service";
        when(serviceUrlConfig.media()).thenReturn(mediaUrl);

        NoFileMediaVm expectedVm = new NoFileMediaVm(mediaId, "caption", "file.png", "image/png", "url");

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedVm);

        NoFileMediaVm result = mediaService.getMedia(mediaId);

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(mediaId);
        assertThat(result.caption()).isEqualTo("caption");
    }

    @Test
    void removeMedia_ShouldDeleteMedia() {
        Long mediaId = 1L;
        String mediaUrl = "http://media-service";
        when(serviceUrlConfig.media()).thenReturn(mediaUrl);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token");
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        RestClient.RequestHeadersUriSpec requestHeadersUriSpec = mock(RestClient.RequestHeadersUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.delete()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(any(java.net.URI.class))).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(Void.class)).thenReturn(null);

        mediaService.removeMedia(mediaId);
        
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
    
    @Test
    void saveFile_ShouldReturnMediaVm() {
        String mediaUrl = "http://media-service";
        when(serviceUrlConfig.media()).thenReturn(mediaUrl);

        Jwt jwt = mock(Jwt.class);
        when(jwt.getTokenValue()).thenReturn("token");
        org.springframework.security.core.Authentication authentication = mock(org.springframework.security.core.Authentication.class);
        when(authentication.getPrincipal()).thenReturn(jwt);
        org.springframework.security.core.context.SecurityContext securityContext = mock(org.springframework.security.core.context.SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        org.springframework.security.core.context.SecurityContextHolder.setContext(securityContext);

        org.springframework.web.multipart.MultipartFile multipartFile = mock(org.springframework.web.multipart.MultipartFile.class);
        org.springframework.core.io.Resource resource = mock(org.springframework.core.io.Resource.class);
        when(multipartFile.getResource()).thenReturn(resource);

        NoFileMediaVm expectedVm = new NoFileMediaVm(1L, "caption", "file.png", "image/png", "url");

        RestClient.RequestBodyUriSpec requestBodyUriSpec = mock(RestClient.RequestBodyUriSpec.class);
        RestClient.ResponseSpec responseSpec = mock(RestClient.ResponseSpec.class);

        when(restClient.post()).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.uri(any(java.net.URI.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.contentType(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.headers(any())).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.body(any(org.springframework.util.MultiValueMap.class))).thenReturn(requestBodyUriSpec);
        when(requestBodyUriSpec.retrieve()).thenReturn(responseSpec);
        when(responseSpec.body(NoFileMediaVm.class)).thenReturn(expectedVm);

        NoFileMediaVm result = mediaService.saveFile(multipartFile, "caption", "fileName");

        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.caption()).isEqualTo("caption");
        
        org.springframework.security.core.context.SecurityContextHolder.clearContext();
    }
}
