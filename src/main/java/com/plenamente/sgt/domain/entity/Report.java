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
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idReport;
    @ManyToOne
    @JoinColumn(name = "id_medical_history")
    private MedicalHistory medicalHistory;
    private String name;
    private String description;
}
