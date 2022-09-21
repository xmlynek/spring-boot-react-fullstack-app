package com.filip.managementapp.dto;

public record ImageFileDto(String filename,
                           String contentType,
                           byte[] data) {
}
