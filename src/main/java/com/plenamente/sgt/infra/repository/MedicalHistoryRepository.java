package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    List<MedicalHistory> findByPatient_IdPatient(Long patientId);
}
