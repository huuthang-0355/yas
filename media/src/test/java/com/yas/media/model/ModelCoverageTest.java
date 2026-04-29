package com.yas.media.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.yas.media.model.dto.MediaDto;
import java.io.ByteArrayInputStream;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

public class ModelCoverageTest {

    @Test
    void testMediaEntity() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("caption");
        media.setFileName("filename");
        media.setFilePath("filepath");
        media.setMediaType("type");

        assertEquals(1L, media.getId());
        assertEquals("caption", media.getCaption());
        assertEquals("filename", media.getFileName());
        assertEquals("filepath", media.getFilePath());
        assertEquals("type", media.getMediaType());
    }

    @Test
    void testMediaDto() {
        ByteArrayInputStream stream = new ByteArrayInputStream("content".getBytes());
        MediaDto dto = MediaDto.builder()
            .content(stream)
            .mediaType(MediaType.IMAGE_JPEG)
            .build();

        assertNotNull(dto);
        assertEquals(stream, dto.getContent());
        assertEquals(MediaType.IMAGE_JPEG, dto.getMediaType());
    }
}
