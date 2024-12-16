package com.plenamente.sgt.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.awt.geom.Area;

@Entity(name = "MaterialArea")
@Table(name = "material_areas")
@Getter
@Setter
@NoArgsConstructor
public class MaterialArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMaterialArea;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "intervention_area_id")
    @JsonBackReference
    private InterventionArea interventionArea;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "material_area_id")
    private Material material;

}