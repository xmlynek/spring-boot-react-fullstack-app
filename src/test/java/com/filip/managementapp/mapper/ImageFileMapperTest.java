package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.ImageFileDto;
import com.filip.managementapp.model.ImageFile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class ImageFileMapperTest {

    private final ImageFileMapper imageFileMapper;

    private ImageFile imageFile;

    private ImageFileDto imageFileDto;

    public ImageFileMapperTest() {
        this.imageFileMapper = Mappers.getMapper(ImageFileMapper.class);
    }

    @BeforeEach
    void setUp() {
        this.imageFile = new ImageFile(
                1L,
                "file.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "data".getBytes()
        );
        this.imageFileDto = new ImageFileDto(
                imageFile.getFilename(),
                imageFile.getContentType(),
                imageFile.getData()
        );
    }

    @Test
    void imageFileToImageFileDto() {
        ImageFileDto output = imageFileMapper.imageFileToImageFileDto(imageFile);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrProperties()
                .isEqualTo(imageFileDto);
        assertArrayEquals(output.data(), imageFile.getData());
    }

    @Test
    void imageFileToImageFileDtoShouldReturnNull() {
        assertThat(imageFileMapper.imageFileToImageFileDto(null)).isNull();
    }

    @Test
    void imageFileDtoToImageFile() {
        ImageFile output = imageFileMapper.imageFileDtoToImageFile(imageFileDto);
        imageFile.setId(null);

        assertThat(output)
                .isNotNull()
                .hasNoNullFieldsOrPropertiesExcept("id")
                .isEqualTo(imageFile);
    }

    @Test
    void imageFileDtoToImageFileShouldReturnNull() {
        assertThat(imageFileMapper.imageFileDtoToImageFile(null)).isNull();
    }

    @Test
    void shouldMapMultipartFileToImageFile() throws IOException {
        MultipartFile multipartFile = new MockMultipartFile(
                "file",
                imageFile.getFilename(),
                imageFile.getContentType(),
                imageFile.getData()
        );
        imageFile.setId(null);

        ImageFile output = imageFileMapper.multipartFileToImageFile(multipartFile);

        assertThat(output)
                .isNotNull()
                .isEqualTo(imageFile);
    }

    @Test
    void multipartFileToImageFileShouldReturnNull() throws IOException {
        assertThat(imageFileMapper.multipartFileToImageFile(null)).isNull();
    }
}