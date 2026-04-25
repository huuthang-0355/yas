package com.yas.media.utils;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import jakarta.validation.ConstraintValidatorContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;

class FileTypeValidatorTest {

    private FileTypeValidator validator;
    private ConstraintValidatorContext context;
    private ConstraintValidatorContext.ConstraintViolationBuilder builder;

    @BeforeEach
    void setUp() {
        validator = new FileTypeValidator();
        ValidFileType annotation = mock(ValidFileType.class);
        when(annotation.allowedTypes()).thenReturn(new String[]{"image/jpeg", "image/png"});
        when(annotation.message()).thenReturn("Invalid type");
        validator.initialize(annotation);

        context = mock(ConstraintValidatorContext.class);
        builder = mock(ConstraintValidatorContext.ConstraintViolationBuilder.class);
        when(context.buildConstraintViolationWithTemplate("Invalid type")).thenReturn(builder);
        when(builder.addConstraintViolation()).thenReturn(context);
    }

    @Test
    void isValid_whenFileIsNull_returnsFalse() {
        assertFalse(validator.isValid(null, context));
    }

    @Test
    void isValid_whenContentTypeIsNull_returnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", null, "test".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenContentTypeIsNotAllowed_returnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.txt", "text/plain", "test".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsInvalidImage_returnsFalse() {
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", "not-an-image".getBytes());
        assertFalse(validator.isValid(file, context));
    }

    @Test
    void isValid_whenFileIsValidImage_returnsTrue() throws IOException {
        BufferedImage img = new BufferedImage(1, 1, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(img, "png", baos);
        MultipartFile file = new MockMultipartFile("file", "test.png", "image/png", baos.toByteArray());
        assertTrue(validator.isValid(file, context));
    }
}
