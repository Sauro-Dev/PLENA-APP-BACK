package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Entity(name = "Report")
@Table(name = "reports")
@Getter
@Setter
@RequiredArgsConstructor
public class Report extends DocumentMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medical_history", nullable = false)
    private MedicalHistory medicalHistory;

    @Column(nullable = false)
    private Integer treatmentMonth;
}