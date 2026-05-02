package com.yas.media.viewmodel;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockMultipartFile;

public class ViewModelCoverageTest {

    @Test
    void testErrorVm() {
        ErrorVm error1 = new ErrorVm("400", "title", "detail");
        ErrorVm error2 = new ErrorVm("400", "title", "detail", List.of("error"));

        assertEquals("400", error1.statusCode());
        assertEquals("400", error2.statusCode());
        assertEquals("title", error1.title());
        assertEquals("detail", error1.detail());
        assertNotNull(error1.fieldErrors());
        assertEquals(1, error2.fieldErrors().size());
    }

    @Test
    void testMediaPostVm() {
        MockMultipartFile file = new MockMultipartFile("file", "test".getBytes());
        MediaPostVm vm = new MediaPostVm("caption", file, "override");

        assertEquals("caption", vm.caption());
        assertEquals(file, vm.multipartFile());
        assertEquals("override", vm.fileNameOverride());
    }

    @Test
    void testMediaVm() {
        MediaVm vm = new MediaVm(1L, "caption", "file", "type", "url");
        vm.setId(2L);
        vm.setCaption("new_caption");
        vm.setFileName("new_file");
        vm.setMediaType("new_type");
        vm.setUrl("new_url");

        assertEquals(2L, vm.getId());
        assertEquals("new_caption", vm.getCaption());
        assertEquals("new_file", vm.getFileName());
        assertEquals("new_type", vm.getMediaType());
        assertEquals("new_url", vm.getUrl());
    }

    @Test
    void testNoFileMediaVm() {
        NoFileMediaVm vm = new NoFileMediaVm(1L, "caption", "file", "type");
        assertEquals(1L, vm.id());
        assertEquals("caption", vm.caption());
        assertEquals("file", vm.fileName());
        assertEquals("type", vm.mediaType());
    }
}
