package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.MedicalHistoryDto.ListMedicalHistory;
import com.plenamente.sgt.domain.dto.MedicalHistoryDto.RegisterMedicalHistory;
import com.plenamente.sgt.domain.entity.MedicalHistory;

import java.util.List;

public interface MedicalHistoryService {
    MedicalHistory createMedicalHistory(RegisterMedicalHistory medicalHistory);
    List<ListMedicalHistory> getReport_DocumentByPatientId(Long id);
}
