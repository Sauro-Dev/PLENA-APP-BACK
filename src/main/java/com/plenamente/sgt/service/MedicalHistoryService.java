package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;

import java.util.List;

public interface MedicalHistoryService {
    List<ListMedicalHistory> getReport_DocumentByPatientId(Long patientId);
}
