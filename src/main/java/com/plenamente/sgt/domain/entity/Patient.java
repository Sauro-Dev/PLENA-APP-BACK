package com.plenamente.sgt.domain.entity;


import com.fasterxml.jackson.annotation.JsonManagedReference;
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
    private String dni;
    private LocalDate birthdate;
    private int age;
    private String presumptiveDiagnosis;

    @ManyToOne
    @JoinColumn(name = "plan_id", nullable = false)
    private Plan idPlan;

    @OneToMany(mappedBy = "patient", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<Tutor> tutors;

    private boolean status = true;
}
