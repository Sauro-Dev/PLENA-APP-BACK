package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.EvaluationDocument;

import com.plenamente.sgt.domain.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EvaluationDocumentRepository extends JpaRepository<EvaluationDocument, Long> {
    List<EvaluationDocument> findByMedicalHistory_IdMedicalHistory(Long medicalHistoryId);
    List<EvaluationDocument> findByMedicalHistory(MedicalHistory medicalHistory);
}
