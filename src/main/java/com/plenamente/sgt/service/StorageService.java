package com.plenamente.sgt.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    String store(MultipartFile file, String directory);
    Resource loadAsResource(String filename);
    void delete(String filename);
    String getFileUrl(String filename);
}