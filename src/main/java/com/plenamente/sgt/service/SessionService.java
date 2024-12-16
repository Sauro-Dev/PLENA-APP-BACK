package com.plenamente.sgt.service;

import com.plenamente.sgt.domain.dto.SessionDto.ListSession;
import com.plenamente.sgt.domain.dto.SessionDto.MarkPresenceSession;
import com.plenamente.sgt.domain.dto.SessionDto.RegisterSession;
import com.plenamente.sgt.domain.dto.SessionDto.UpdateSession;
import com.plenamente.sgt.domain.dto.UserDto.ListTherapist;
import com.plenamente.sgt.domain.entity.Session;
import com.plenamente.sgt.domain.entity.Therapist;
import com.plenamente.sgt.domain.entity.User;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface SessionService {
    Session createSession(RegisterSession dto);
    Session updateSession(UpdateSession dto);
    List<ListSession> getSessionsByDate(LocalDate date);
    List<ListSession> getSessionsByTherapist(Long therapistId);
    Session markPresence(MarkPresenceSession dto);
    void assignSessionsFromSession(Long sessionId);
    List<ListTherapist> getAvailableTherapist(LocalDate date, LocalTime startTime, LocalTime endTime);
}
