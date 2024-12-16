package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity(name="PatientPayment")
@Table(name="patient_payment")
@Getter
@Setter
@NoArgsConstructor
public class PatientPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatientPayment;
    private String paymentMethod;
    private String paymentType;
    private Integer paymentFee;
    private Double amount;
    private LocalDateTime date;

    @ManyToOne()
    @JoinColumn(name = "patient_id")
    private Patient idPatient;
}
