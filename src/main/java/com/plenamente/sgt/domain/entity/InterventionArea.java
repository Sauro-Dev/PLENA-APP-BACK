package com.plenamente.sgt.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Entity(name = "InterventionArea")
@Table(name = "intervention_areas")
@Getter
@Setter
@NoArgsConstructor
@Data
public class InterventionArea {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idInterventionArea;
    private String name;
    private String description;

    @JsonManagedReference
    @OneToMany(mappedBy = "interventionArea", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<MaterialArea> materialAreas;
}