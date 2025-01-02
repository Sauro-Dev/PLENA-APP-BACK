package com.plenamente.sgt.infra.repository;

import com.plenamente.sgt.domain.entity.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoomRepository extends JpaRepository<Room, Long> {
    List<Room> findByEnabledTrue();
    List<Room> findByIsTherapeuticAndEnabledTrue(boolean isTherapeutic);
    Optional<Room> findByIdRoomAndEnabledTrue(Long idRoom);
}
