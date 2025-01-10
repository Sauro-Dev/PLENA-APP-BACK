package com.plenamente.sgt.service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

public interface StorageService {
    // Almacena un archivo en el sistema de archivos y devuelve el nombre del archivo almacenado
    String store(MultipartFile file, String directory);
    // Carga un archivo como recurso
    Resource loadAsResource(String filename);
    // Elimina un archivo almacenado
    void delete(String filename);
    // Devuelve la URL de un archivo almacenado
    String getFileUrl(String filename);
}