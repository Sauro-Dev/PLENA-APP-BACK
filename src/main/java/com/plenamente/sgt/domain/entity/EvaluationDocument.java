package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "EvaluationDocument")
@Table(name = "evaluation_document")
@Getter
@Setter
@NoArgsConstructor
public class EvaluationDocument {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idDocument;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_medical_history")
    private MedicalHistory medicalHistory;
    private String name;
    private String description;
    private String documentType;
    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] archive;
}
