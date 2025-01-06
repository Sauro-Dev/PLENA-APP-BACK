package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.EvaluationDocumentDetailsDto;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.RegisterEvaluationDocument;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.UpdateEvaluationDocument;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EvaluationDocumentService {
    EvaluationDocument createEvaluationDocument(RegisterEvaluationDocument evaluationDocument, MultipartFile file);
    UpdateEvaluationDocument updateEvaluationDocument(Long id, UpdateEvaluationDocument evaluationDocumentUp, MultipartFile newFile);
    EvaluationDocument downloadEvaluationDocument(Long id);
    EvaluationDocumentDetailsDto findEvaluationDocumentById(Long id);
    List<EvaluationDocumentDetailsDto> findDocumentsByMedicalHistoryId(Long medicalHistoryId);
}
