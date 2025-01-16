package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import com.plenamente.sgt.infra.exception.StorageFileNotFoundException;
import com.plenamente.sgt.service.EvaluationDocumentService;
import com.plenamente.sgt.service.StorageService;
import com.plenamente.sgt.service.storage.StorageProperties;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/evaluationDocument")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Evaluation Documents", description = "API endpoints for managing evaluation documents")
public class EvaluationDocumentController {

    private final EvaluationDocumentService documentService;
    private final StorageService storageService;
    private final StorageProperties storageProperties;

    @PostMapping("/patient/{patientId}/medical-history/{medicalHistoryId}/upload")
    public ResponseEntity<EvaluationDocumentDto> uploadDocument(
            @PathVariable Long patientId,
            @PathVariable Long medicalHistoryId,
            @RequestParam("file") MultipartFile file,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description) {

        if (file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        return ResponseEntity.ok(documentService.uploadDocument(
                patientId, medicalHistoryId, file, name, description));
    }

    @GetMapping("/medical-history/{medicalHistoryId}")
    public ResponseEntity<List<EvaluationDocumentDto>> getDocuments(
            @PathVariable Long medicalHistoryId) {
        List<EvaluationDocumentDto> documents = documentService
                .getDocumentsByMedicalHistory(medicalHistoryId);
        return ResponseEntity.ok(documents);
    }

    @GetMapping("/{documentId}")
    public ResponseEntity<EvaluationDocumentDto> getDocument(@PathVariable Long documentId) {
        return ResponseEntity.ok(documentService.getDocument(documentId));
    }

    @DeleteMapping("/{documentId}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long documentId) {
        documentService.deleteDocument(documentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medical-history/{medicalHistoryId}/document/{documentId}/download")
    public ResponseEntity<Resource> downloadDocument(
            @PathVariable Long medicalHistoryId,
            @PathVariable Long documentId) {
        try {
            log.debug("Iniciando descarga de documento. ID: {}, MedicalHistoryId: {}",
                    documentId, medicalHistoryId);

            EvaluationDocumentDto document = documentService.getDocument(documentId);
            log.debug("Documento recuperado: {}", document);

            if (!document.getMedicalHistoryId().equals(medicalHistoryId)) {
                log.warn("Intento de acceso a documento de otro historial médico");
                throw new IllegalArgumentException("El documento no pertenece al historial médico especificado");
            }

            String relativePath = document.getFileUrl()
                    .replace(storageProperties.getServerUrl() + "/static/", "");
            log.debug("Ruta relativa del archivo: {}", relativePath);

            Resource file = storageService.loadAsResource(relativePath);

            if (file == null || !file.exists()) {
                throw new StorageFileNotFoundException("Archivo no encontrado en el sistema");
            }

            log.debug("Archivo cargado exitosamente: {}", file.getFilename());

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(document.getContentType()))
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + document.getFileName() + "\"")
                    .body(file);

        } catch (StorageFileNotFoundException e) {
            log.error("Archivo no encontrado. DocumentId: {}, MedicalHistoryId: {}",
                    documentId, medicalHistoryId, e);
            throw e;
        } catch (Exception e) {
            log.error("Error al descargar archivo. DocumentId: {}, MedicalHistoryId: {}",
                    documentId, medicalHistoryId, e);
            throw new StorageFileNotFoundException("No se pudo descargar el archivo: " + documentId);
        }
    }
}