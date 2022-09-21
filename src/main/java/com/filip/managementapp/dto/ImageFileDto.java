package com.filip.managementapp.dto;

import java.util.Arrays;
import java.util.Objects;

public record ImageFileDto(String filename,
                           String contentType,
                           byte[] data) {

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageFileDto that = (ImageFileDto) o;
        return filename.equals(that.filename) && contentType.equals(that.contentType) && Arrays.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        int result = Objects.hash(filename, contentType);
        result = 31 * result + Arrays.hashCode(data);
        return result;
    }

    @Override
    public String toString() {
        return "ImageFileDto{" +
                "filename='" + filename + '\'' +
                ", contentType='" + contentType + '\'' +
                ", dataLength='" + data.length + '\'' +
                '}';
    }
}
