package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.infra.exception.ResourceNotFoundException;
import com.plenamente.sgt.infra.repository.EvaluationDocumentRepository;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.service.EvaluationDocumentService;
import com.plenamente.sgt.service.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
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
    private final ModelMapper modelMapper;
    private final StorageService storageService;
    private final PatientRepository patientRepository;

    @CachePut(key = "#result.idDocument")
    @Override
    public EvaluationDocumentDto uploadDocument(Long patientId, Long medicalHistoryId, MultipartFile file, EvaluationDocumentDto dto) {
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found with id: " + patientId));

        // Verificar que el historial médico existe y pertenece al paciente
        MedicalHistory medicalHistory = medicalHistoryRepository.findById(medicalHistoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Medical History not found with id: " + medicalHistoryId));

        if (!medicalHistory.getPatient().getIdPatient().equals(patientId)) {
            throw new IllegalArgumentException("Medical History does not belong to the specified patient");
        }

        // Crear el directorio incluyendo el ID del paciente
        String directory = String.format("patients/%d/medical-history/%d/evaluations", patientId, medicalHistoryId);
        String storedFilename = storageService.store(file, directory);
        String fileUrl = storageService.getFileUrl(storedFilename);

        EvaluationDocument document = new EvaluationDocument();
        document.setMedicalHistory(medicalHistory);
        document.setName(dto.getName());
        document.setDescription(dto.getDescription());
        document.setFileUrl(fileUrl);
        document.setFileName(file.getOriginalFilename());
        document.setContentType(file.getContentType());
        document.setFileSize(file.getSize());
        document.setUploadAt(LocalDateTime.now());
        document.setEvaluationType(dto.getEvaluationType());
        document.setEvaluationDate(LocalDate.from(dto.getEvaluationDate()));

        document = documentRepository.save(document);
        return mapToDto(document);
    }

    @Cacheable(key = "#documentId")
    @Override
    @Transactional(readOnly = true)
    public EvaluationDocumentDto getDocument(Long documentId) {
        return documentRepository.findById(documentId)
                .map(this::mapToDto)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));
    }

    @CacheEvict(key = "#documentId")
    @Override
    public void deleteDocument(Long documentId) {
        EvaluationDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException("Document not found with id: " + documentId));

        try {
            storageService.delete(document.getFileUrl());
        } catch (Exception e) {
            log.error("Error deleting file: {}", document.getFileUrl(), e);
        }

        documentRepository.delete(document);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(key = "'medical_history_' + #medicalHistoryId")
    public List<EvaluationDocumentDto> getDocumentsByMedicalHistory(Long medicalHistoryId) {
        return documentRepository.findByMedicalHistoryIdMedicalHistory(medicalHistoryId)
                .stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private EvaluationDocumentDto mapToDto(EvaluationDocument document) {
        EvaluationDocumentDto dto = modelMapper.map(document, EvaluationDocumentDto.class);
        dto.setMedicalHistoryId(document.getMedicalHistory().getIdMedicalHistory());
        return dto;
    }
}
