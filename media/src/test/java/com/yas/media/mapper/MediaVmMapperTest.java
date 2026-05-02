package com.yas.media.mapper;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

class MediaVmMapperTest {

    @Test
    void testMapperInstantiation() {
        MediaVmMapper mapper = Mappers.getMapper(MediaVmMapper.class);
        assertNotNull(mapper);
    }
}
