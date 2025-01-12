package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.entity.MedicalHistory;
import com.plenamente.sgt.infra.repository.MedicalHistoryRepository;
import com.plenamente.sgt.service.MedicalHistoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Cambiamos a @RequiredArgsConstructor
@Slf4j
public class MedicalHistoryServiceImpl implements MedicalHistoryService {

    private final MedicalHistoryRepository medicalHistoryRepository;

    @Override
    public MedicalHistory createMedicalHistory(RegisterMedicalHistory medicalHistory) {
        return null;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ListMedicalHistory> getReport_DocumentByPatientId(Long patientId) {
        List<MedicalHistory> histories = medicalHistoryRepository.findByPatient_IdPatient(patientId);

        return histories.stream()
                .map(history -> new ListMedicalHistory(
                        history.getReports().isEmpty() ? null : history.getReports().get(0),
                        history.getEvaluationDocuments().isEmpty() ? null : history.getEvaluationDocuments().get(0).getName(),
                        history.getEvaluationDocuments().isEmpty() ? null : history.getEvaluationDocuments().get(0).getDescription(),
                        history.getEvaluationDocuments().isEmpty() ? null : history.getEvaluationDocuments().get(0).getEvaluationType(),
                        null,
                        history.getPatient().getName(),
                        history.getIdMedicalHistory()
                ))
                .collect(Collectors.toList());
    }
}