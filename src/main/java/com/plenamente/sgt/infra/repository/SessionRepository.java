package com.plenamente.sgt.infra.repository;

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

    boolean existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(Long therapistId, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, Long idSession);

    boolean existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThanAndIdSessionNot(Long idRoom, LocalDate sessionDate, LocalTime startTime, LocalTime endTime, Long idSession);

    List<Session> findBySessionDateBetween(LocalDate startDate, LocalDate endDate);

    boolean existsByTherapist_IdUserAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(Long therapistId, LocalDate date, LocalTime startTime, LocalTime endTime);

    boolean existsByRoom_IdRoomAndSessionDateAndEndTimeGreaterThanAndStartTimeLessThan(Long roomId, LocalDate date, LocalTime startTime, LocalTime endTime);

    List<Session> findBySessionDateAndStartTimeLessThanAndEndTimeGreaterThan(LocalDate date, LocalTime endTime, LocalTime startTime);
}
