package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {

    List<Patient> findByNameContainingIgnoreCase(String name);
    List<Patient> findByPaternalSurnameContainingIgnoreCase(String paternalSurname);
    List<Patient> findByIdPlanIdPlan(Long planId);
    List<Patient> findByStatus(boolean status);
    List<Patient> findAllByOrderByNameAsc();
    List<Patient> findAllByOrderByNameDesc();
}
