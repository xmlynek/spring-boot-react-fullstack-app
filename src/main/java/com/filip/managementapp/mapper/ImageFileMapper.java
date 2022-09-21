package com.filip.managementapp.mapper;

import com.filip.managementapp.dto.ImageFileDto;
import com.filip.managementapp.model.ImageFile;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR)
public interface ImageFileMapper {

    ImageFileDto imageFileToImageFileDto(ImageFile imageFile);
    ImageFile imageFileDtoToImageFile(ImageFileDto imageFileDto);

    @Mapping(source = "originalFilename", target = "filename")
    @Mapping(source = "bytes", target = "data")
    ImageFile multipartFileToImageFile(MultipartFile multipartFile) throws IOException;
}
