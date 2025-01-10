package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.entity.EvaluationDocument;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Report;
import com.plenamente.sgt.infra.repository.EvaluationDocumentRepository;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.infra.repository.PatientRepository;
import com.plenamente.sgt.infra.repository.ReportRepository;
import com.plenamente.sgt.service.MedicalHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@NoArgsConstructor
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    @Override
    public MedicalHistory createMedicalHistory(RegisterMedicalHistory medicalHistory) {
        return null;
    }

    @Override
    public List<ListMedicalHistory> getReport_DocumentByPatientId(Long id) {
        return List.of();
    }
}
