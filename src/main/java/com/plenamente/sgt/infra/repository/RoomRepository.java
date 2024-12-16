package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByIsTherapeutic(boolean isTherapeutic);
}