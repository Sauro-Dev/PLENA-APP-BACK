package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.cglib.core.Local;

import java.time.LocalDate;

@Entity(name = "EvaluationDocument")
@Table(name = "evaluation_document")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationDocument extends DocumentMetadata {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medical_history", nullable = false)
    private MedicalHistory medicalHistory;
}