package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.service.storage.StorageProperties;
import com.plenamente.sgt.infra.exception.StorageException;
import com.plenamente.sgt.infra.exception.StorageFileNotFoundException;
import com.plenamente.sgt.service.StorageService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Service
@Slf4j
public class NginxStorageServiceImpl implements StorageService {

    private final StorageProperties storageProperties;

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(storageProperties.getLocation());
            log.info("Storage location initialized at: {}", storageProperties.getLocation());
        } catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public String store(MultipartFile file, String directory) {
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file");
            }

            Path targetLocation = storageProperties.getLocation().resolve(directory);
            Files.createDirectories(targetLocation);

            String uniqueFilename = generateUniqueFilename(
                    StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()))
            );
            Path destinationFile = targetLocation.resolve(uniqueFilename);

            log.debug("Storing file to: {}", destinationFile);

            // Transferencia directa del archivo
            file.transferTo(destinationFile);
            log.info("File stored successfully at: {}", destinationFile);

            return directory + "/" + uniqueFilename;
        } catch (IOException e) {
            log.error("Failed to store file", e);
            throw new StorageException("Failed to store file", e);
        }
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            // Si la URL es completa, extraemos solo la parte relativa
            String relativePath = filename;
            if (filename.startsWith(storageProperties.getServerUrl())) {
                relativePath = filename.replace(storageProperties.getServerUrl() + "/static/", "");
            }

            log.debug("Intentando cargar archivo con ruta relativa: {}", relativePath);
            Path file = storageProperties.getLocation().resolve(relativePath);
            log.debug("Ruta completa del archivo: {}", file.toAbsolutePath());

            Resource resource = new UrlResource(file.toUri());

            if (resource.exists() && resource.isReadable()) {
                log.debug("Archivo encontrado y legible");
                return resource;
            } else {
                log.error("Archivo no encontrado o no legible en: {}", file.toAbsolutePath());
                throw new StorageFileNotFoundException("Could not read file: " + relativePath);
            }
        } catch (MalformedURLException e) {
            log.error("Error al crear URL para el archivo: {}", filename, e);
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        } catch (Exception e) {
            log.error("Error al cargar el archivo: {}", filename, e);
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void delete(String filename) {
        try {
            String relativePath = filename;
            if (filename.startsWith(storageProperties.getServerUrl())) {
                relativePath = filename.replace(storageProperties.getServerUrl() + "/static/", "");
            }

            Path file = storageProperties.getLocation().resolve(relativePath);
            Files.deleteIfExists(file);
            log.debug("Deleted file: {}", relativePath);
        } catch (IOException e) {
            log.error("Could not delete file: {}", filename, e);
            throw new StorageException("Could not delete file: " + filename, e);
        }
    }

    @Override
    public String getFileUrl(String filename) {
        return storageProperties.getServerUrl() + "/static/" + filename;
    }

    private String generateUniqueFilename(String originalFilename) {
        String baseName = FilenameUtils.getBaseName(originalFilename);
        String extension = FilenameUtils.getExtension(originalFilename);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String random = UUID.randomUUID().toString().substring(0, 8);

        return String.format("%s_%s_%s.%s", baseName, timestamp, random, extension);
    }
}
