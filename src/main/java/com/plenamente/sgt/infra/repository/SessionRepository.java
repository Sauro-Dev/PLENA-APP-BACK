package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Patient;
import com.plenamente.sgt.domain.entity.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySessionDate(LocalDate date);
    List<Session> findByTherapist_IdUser(Long therapistId);
    Optional<Session> findByIdSession(Long idSession);
    Optional<Session> findById(Long sessionId);

    boolean existsByTherapist_IdUserAndSessionDateAndStartTimeLessThanEqualAndEndTimeGreaterThanEqual(Long therapistId, LocalDate sessionDate, LocalTime endTime, LocalTime startTime);

}
