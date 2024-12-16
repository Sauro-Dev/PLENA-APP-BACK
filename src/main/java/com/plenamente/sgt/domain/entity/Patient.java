package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity(name = "Patient")
@Table(name = "patients")
@Getter
@Setter
@NoArgsConstructor
public class Patient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPatient;
    private String name;
    private String paternalSurname;
    private String maternalSurname;
    private LocalDate birthdate;
    private int age;
    private String allergies;



    private boolean status = true;

    @ManyToOne()
    @JoinColumn(name = "plan_id")
    private Plan idPlan;

    @OneToMany
    @JoinColumn(name = "patient_id")
    private List<Tutor> tutors;
}
