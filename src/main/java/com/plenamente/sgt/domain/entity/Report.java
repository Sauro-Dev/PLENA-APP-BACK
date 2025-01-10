package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name = "Report")
@Table(name = "reports")
@Getter
@Setter
@RequiredArgsConstructor
public class Report extends DocumentMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;
    @ManyToOne
    @JoinColumn(name = "id_medical_history")
    private MedicalHistory medicalHistory;
    private LocalDateTime reportPeriodStart;
    private LocalDateTime reportPeriodEnd;
    private String reportType;
}
