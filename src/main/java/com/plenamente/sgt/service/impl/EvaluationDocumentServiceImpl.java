package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.infra.exception.FileStorageException;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.exception.StorageException;
import com.plenamente.sgt.infra.exception.StorageFileNotFoundException;
import com.plenamente.sgt.infra.repository.EvaluationDocumentRepository;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.service.EvaluationDocumentService;
import com.plenamente.sgt.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.service.spi.ServiceException;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@Slf4j
@RequiredArgsConstructor
@CacheConfig(cacheNames = "evaluation_documents")
public class EvaluationDocumentServiceImpl implements EvaluationDocumentService {
    private final EvaluationDocumentRepository documentRepository;
    private final MedicalHistoryRepository medicalHistoryRepository;
    private final PatientRepository patientRepository;
    private final StorageService storageService;
    private final ModelMapper modelMapper;

    @Override
    @CacheEvict(cacheNames = "evaluation_documents", key = "'medical_history_' + #medicalHistoryId")
    public EvaluationDocumentDto uploadDocument(Long patientId, Long medicalHistoryId,
                                                MultipartFile file, String name, String description) {
        try {
            MedicalHistory medicalHistory = validateAndGetMedicalHistory(patientId, medicalHistoryId);
            String directory = String.format("patients/%d/medical-history/%d/evaluations",
                    patientId, medicalHistoryId);

            String storedFilename = storageService.store(file, directory);
            String fileUrl = storageService.getFileUrl(storedFilename);

            EvaluationDocument document = new EvaluationDocument();
            document.setMedicalHistory(medicalHistory);
            document.setName(name);
            document.setDescription(description);
            document.setFileUrl(fileUrl);
            document.setFileName(file.getOriginalFilename());
            document.setContentType(file.getContentType());
            document.setFileSize(file.getSize());
            document.setUploadAt(LocalDateTime.now());

            document = documentRepository.save(document);
            log.debug("Documento guardado y caché limpiado para historial médico: {}", medicalHistoryId);

            return modelMapper.map(document, EvaluationDocumentDto.class);
        } catch (StorageException e) {
            log.error("Error storing file: {}", e.getMessage());
            throw new FileStorageException("Error storing file: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error uploading document: {}", e.getMessage());
            throw new ServiceException("Error uploading document", e);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public EvaluationDocumentDto getDocument(Long documentId) {
        log.debug("Buscando documento con ID: {}", documentId);
        return documentRepository.findById(documentId)
                .map(doc -> {
                    try {
                        EvaluationDocumentDto dto = new EvaluationDocumentDto();
                        // Mapeo manual de los campos heredados de DocumentMetadataDto
                        dto.setName(doc.getName());
                        dto.setDescription(doc.getDescription());
                        dto.setFileUrl(doc.getFileUrl());
                        dto.setFileName(doc.getFileName());
                        dto.setContentType(doc.getContentType());
                        dto.setFileSize(doc.getFileSize());
                        dto.setUploadAt(doc.getUploadAt());

                        // Mapeo de los campos propios de EvaluationDocumentDto
                        dto.setIdDocument(doc.getIdDocument());
                        dto.setMedicalHistoryId(doc.getMedicalHistory().getIdMedicalHistory());

                        return dto;
                    } catch (Exception e) {
                        log.error("Error mapeando documento a DTO: {}", e.getMessage());
                        throw new ServiceException("Error al procesar el documento");
                    }
                })
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    @Override
    @CacheEvict(cacheNames = "evaluation_documents", allEntries = true)
    public void deleteDocument(Long documentId) {
        try {
            EvaluationDocument document = documentRepository.findById(documentId)
                    .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

            storageService.delete(document.getFileUrl());
            documentRepository.delete(document);
        } catch (StorageFileNotFoundException e) {
            log.warn("File not found while deleting document: {}", e.getMessage());
            // Continuar con la eliminación del registro en la base de datos
            documentRepository.deleteById(documentId);
        } catch (StorageException e) {
            log.error("Error deleting file: {}", e.getMessage());
            throw new FileStorageException("Error deleting file", e);
        }
    }

    @Override
    @Cacheable(key = "'medical_history_' + #medicalHistoryId", unless = "#result.empty")
    @Transactional(readOnly = true)
    public List<EvaluationDocumentDto> getDocumentsByMedicalHistory(Long medicalHistoryId) {
        List<EvaluationDocument> documents = documentRepository
                .findByMedicalHistoryIdMedicalHistory(medicalHistoryId);

        return documents.stream()
                .map(doc -> modelMapper.map(doc, EvaluationDocumentDto.class))
                .collect(Collectors.toList());
    }

    private MedicalHistory validateAndGetMedicalHistory(Long patientId, Long medicalHistoryId) {
        patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        MedicalHistory medicalHistory = medicalHistoryRepository.findById(medicalHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical History not found with id: " + medicalHistoryId));

        if (!medicalHistory.getPatient().getIdPatient().equals(patientId)) {
            throw new IllegalArgumentException("Medical History does not belong to the specified patient");
        }

        return medicalHistory;
    }
}
