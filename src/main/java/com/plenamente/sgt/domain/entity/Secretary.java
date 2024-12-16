package com.plenamente.sgt.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity(name = "Secretary")
@Table(name = "secretaries")
@Getter
@Setter
@NoArgsConstructor
public class Secretary extends User {
    private double paymentMonthly;
}