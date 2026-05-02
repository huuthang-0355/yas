package com.yas.media;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;

public class MediaApplicationTest {

    @Test
    void testMediaApplicationInstantiation() {
        MediaApplication app = new MediaApplication();
        assertNotNull(app);
    }
}
