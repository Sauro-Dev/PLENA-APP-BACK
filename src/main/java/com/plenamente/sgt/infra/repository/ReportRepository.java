package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Report;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByMedicalHistoryIdMedicalHistory(Long medicalHistoryId);

    @Query("SELECT r FROM Report r WHERE r.medicalHistory.idMedicalHistory = :medicalHistoryId")
    List<Report> findReportsByMedicalHistoryId(@Param("medicalHistoryId") Long medicalHistoryId);
}
