package com.dlc.modules.api.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * 文件处理api
 */
public interface FileService {
    File upload(MultipartFile file, String path);

    File updateVersioin(Long userId);
}
