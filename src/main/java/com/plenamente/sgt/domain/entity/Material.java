package com.plenamente.sgt.domain.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "materials")
@Getter
@Setter
@NoArgsConstructor
public class Material {
    @Id
    @Column(length = 4)
    private String idMaterial;

    private String nombre;

    private String descripcion;

    private int stock;

    private boolean esCompleto;

    private boolean esSoporte;

    @Enumerated(EnumType.STRING)
    private MaterialStatus estado;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id")
    private Room room;

    @Column(name = "fecha_alta", updatable = false)
    private LocalDateTime fechaAlta;


    @PrePersist
    public void prePersist() {
        this.fechaAlta = LocalDateTime.now();
    }
}