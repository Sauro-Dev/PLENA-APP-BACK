package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


public interface EvaluationDocumentService {
     EvaluationDocumentDto uploadDocument(Long patientId, Long medicalHistoryId, MultipartFile file, EvaluationDocumentDto dto);;
     EvaluationDocumentDto getDocument(Long documentId);
     void deleteDocument(Long documentId);
     List<EvaluationDocumentDto> getDocumentsByMedicalHistory(Long medicalHistoryId);
}
