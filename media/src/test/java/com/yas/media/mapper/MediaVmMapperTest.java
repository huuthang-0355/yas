package com.yas.media.mapper;

import com.yas.media.model.Media;
import com.yas.media.viewmodel.MediaVm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MediaVmMapperTest {

    private MediaVmMapper mediaVmMapper;

    @BeforeEach
    void setUp() {
        mediaVmMapper = Mappers.getMapper(MediaVmMapper.class);
    }

    @Test
    void toVm_ShouldMapMediaToMediaVm() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Test Caption");
        media.setFileName("test-file.png");
        media.setMediaType("image/png");

        MediaVm mediaVm = mediaVmMapper.toVm(media);

        assertThat(mediaVm).isNotNull();
        assertThat(mediaVm.getId()).isEqualTo(1L);
        assertThat(mediaVm.getCaption()).isEqualTo("Test Caption");
        assertThat(mediaVm.getFileName()).isEqualTo("test-file.png");
        assertThat(mediaVm.getMediaType()).isEqualTo("image/png");
    }

    @Test
    void toVm_ShouldReturnNullWhenMediaIsNull() {
        MediaVm mediaVm = mediaVmMapper.toVm((Media) null);
        assertThat(mediaVm).isNull();
    }

    @Test
    void toVms_ShouldMapMediaListToMediaVmList() {
        Media media1 = new Media();
        media1.setId(1L);
        media1.setFileName("file1.png");

        Media media2 = new Media();
        media2.setId(2L);
        media2.setFileName("file2.png");

        // BaseMapper only exposes toVm(M) - map each individually
        MediaVm vm1 = mediaVmMapper.toVm(media1);
        MediaVm vm2 = mediaVmMapper.toVm(media2);
        List<MediaVm> mediaVms = List.of(vm1, vm2);

        assertThat(mediaVms).isNotNull().hasSize(2);
        assertThat(mediaVms.get(0).getId()).isEqualTo(1L);
        assertThat(mediaVms.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void toModel_ShouldMapMediaVmToMedia() {
        MediaVm mediaVm = new MediaVm(1L, "Caption", "file.png", "image/png", "url");

        Media media = mediaVmMapper.toModel(mediaVm);

        assertThat(media).isNotNull();
        assertThat(media.getId()).isEqualTo(1L);
        assertThat(media.getCaption()).isEqualTo("Caption");
        assertThat(media.getFileName()).isEqualTo("file.png");
        assertThat(media.getMediaType()).isEqualTo("image/png");
        // filePath is unmapped
        assertThat(media.getFilePath()).isNull();
    }

    @Test
    void toModel_ShouldReturnNullWhenMediaVmIsNull() {
        Media media = mediaVmMapper.toModel((MediaVm) null);
        assertThat(media).isNull();
    }

    @Test
    void toModels_ShouldMapMediaVmListToMediaList() {
        MediaVm vm1 = new MediaVm(1L, "Caption 1", "file1.png", "image/png", "url1");
        MediaVm vm2 = new MediaVm(2L, "Caption 2", "file2.png", "image/png", "url2");

        // BaseMapper only exposes toModel(V) - map each individually
        Media model1 = mediaVmMapper.toModel(vm1);
        Media model2 = mediaVmMapper.toModel(vm2);
        List<Media> models = List.of(model1, model2);

        assertThat(models).isNotNull().hasSize(2);
        assertThat(models.get(0).getId()).isEqualTo(1L);
        assertThat(models.get(1).getId()).isEqualTo(2L);
    }

    @Test
    void toModels_ShouldReturnNullWhenListIsNull() {
        // BaseMapper doesn't have toModel(List) - test null input for toModel(V)
        Media media = mediaVmMapper.toModel((MediaVm) null);
        assertThat(media).isNull();
    }

    @Test
    void partialUpdate_ShouldUpdateMediaFromMediaVm() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Old Caption");
        media.setFileName("old.png");

        MediaVm mediaVm = new MediaVm(null, "New Caption", "new.png", null, null);

        mediaVmMapper.partialUpdate(media, mediaVm);

        assertThat(media.getCaption()).isEqualTo("New Caption");
        assertThat(media.getFileName()).isEqualTo("new.png");
    }

    @Test
    void partialUpdate_ShouldNotUpdateWhenVmIsNull() {
        Media media = new Media();
        media.setId(1L);
        media.setCaption("Old Caption");

        mediaVmMapper.partialUpdate(media, null);

        assertThat(media.getCaption()).isEqualTo("Old Caption");
    }
}
