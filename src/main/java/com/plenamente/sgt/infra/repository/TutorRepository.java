package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Tutor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TutorRepository extends JpaRepository <Tutor, Long>{
}
