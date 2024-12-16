package com.plenamente.sgt.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name = "AdminTherapist")
@Table(name = "admin_therapists")
@Getter
@Setter
@NoArgsConstructor
public class AdminTherapist extends Admin {
    private Double paymentSession;
}