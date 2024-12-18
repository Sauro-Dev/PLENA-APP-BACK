package com.plenamente.sgt.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "Therapist")
@Table(name = "therapists")
@Getter
@Setter
@NoArgsConstructor
public class Therapist extends User {
    @Column(nullable = false)
    private Double paymentSession;
}