package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.PatientPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PatientPaymentRepository extends JpaRepository<PatientPayment, Long>{
}
