package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findByMedicalHistory_IdMedicalHistory(Long id);
}
