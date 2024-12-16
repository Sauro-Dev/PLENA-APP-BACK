package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.RegisterEvaluationDocument;
import com.plenamente.sgt.domain.dto.EvaluationDocumentDto.UpdateEvaluationDocument;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import org.springframework.web.multipart.MultipartFile;

public interface EvaluationDocumentService {
    EvaluationDocument createEvaluationDocument(RegisterEvaluationDocument evaluationDocument, MultipartFile file);
    EvaluationDocument downloadEvaluationDocument(Long id);
    UpdateEvaluationDocument updateEvaluationDocument(Long id, UpdateEvaluationDocument evaluationDocument);
}
