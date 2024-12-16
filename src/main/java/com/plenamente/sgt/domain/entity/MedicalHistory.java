package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "MedicalHistory")
@Table(name = "medical_historys")
@Getter
@Setter
@NoArgsConstructor
public class MedicalHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMedicalHistory;
    @ManyToOne
    @JoinColumn(name = "id_patient")
    private Patient patient;
    private String name;
}
