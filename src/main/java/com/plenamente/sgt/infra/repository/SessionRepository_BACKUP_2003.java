package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Session;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    List<Session> findBySessionDate(LocalDate date);

    List<Session> findByTherapist_IdUser(Long therapistId);

    Optional<Session> findByIdSession(Long idSession);

    boolean existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(Long therapistId, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, Long idSession);

    boolean existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(Long idRoom, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, Long idSession);

    List<Session> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(
            Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime);

    boolean existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

    List<Session> findBySessionDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime endTime, LocalTime startTime);

    List<Session> findByRoom_IdRoom(Long roomId);

    List<Session> findBySessionDateGreaterThanEqual(LocalDate date);

    List<Session> findByPatient_IdPatient(Long idPatient);

    @Query("SELECT MAX(s.renewPlan) FROM Session s WHERE s.patient.idPatient = :patientId")
    Optional<Integer> findMaxRenewPlanByPatientId(Long patientId);

<<<<<<< HEAD
    @Query("SELECT s FROM Session s WHERE s.sessionDate BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC")
    Page<Session> findBySessionDateBetweenOrderBySessionDateAsc(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.therapist.idUser = :therapistId " +
            "AND s.sessionDate BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC")
    Page<Session> findByTherapistIdAndSessionDateBetweenOrderBySessionDateAsc(
            @Param("therapistId") Long therapistId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);

    @Query("SELECT s FROM Session s WHERE s.patient.idPatient = :patientId " +
            "AND s.sessionDate BETWEEN :startDate AND :endDate ORDER BY s.sessionDate ASC")
    Page<Session> findByPatientIdAndSessionDateBetweenOrderBySessionDateAsc(
            @Param("patientId") Long patientId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);
}
=======
    Optional<Session> findFirstByPatient_IdPatientOrderBySessionDateAsc(Long patientId);
}
>>>>>>> feature/medicalHistoryV2
