package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.infra.repository.MaterialRepository;
import com.plenamente.sgt.infra.repository.RoomRepository;
import com.plenamente.sgt.service.RoomService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {


    private final RoomRepository roomRepository;
    private final MaterialRepository materialRepository;

    @Override
    public List<Material> getMaterialsByRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado con id: " + roomId));

        return materialRepository.findByRoom(room);
    }


    @Override
    public Room registerRoom(Room room) {
        if (room.getName() == null || room.getName().isEmpty()) {
            throw new IllegalArgumentException("Name is required");
        }
        if (room.getAddress() == null || room.getAddress().isEmpty()) {
            throw new IllegalArgumentException("Address is required");
        }
        return roomRepository.save(room);
    }

    @Override
    public List<Room> listRooms() {
        return roomRepository.findByEnabledTrue();
    }

    @Override
    public List<Room> listRoomsByIsTherapeutic(boolean isTherapeutic) {
        return roomRepository.findByIsTherapeuticAndEnabledTrue(isTherapeutic);
    }

    @Override
    public Room getRoomById(Long roomId) {
        return roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado con id: " + roomId));
    }

    @Override
    public Room updateRoom(Long roomId, @RequestBody Room roomUpdated){
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado con id: " + roomId));
        if (roomUpdated.getName() != null) {
            existingRoom.setName(roomUpdated.getName());
        }
        if (roomUpdated.getAddress() != null) {
            existingRoom.setAddress(roomUpdated.getAddress());
        }
        if (roomUpdated.getIsTherapeutic() != null) {
            existingRoom.setIsTherapeutic(roomUpdated.getIsTherapeutic());
        }

        return roomRepository.save(existingRoom);
    }

    @Override
    public void disableRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada con ID: " + roomId));

        if (!room.isEnabled()) {
            throw new IllegalStateException("La sala con ID " + roomId + " ya está deshabilitada.");
        }

        room.setEnabled(false);
        roomRepository.save(room);
    }

    @Override
    public void enableRoom(Long roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Sala no encontrada con ID: " + roomId));

        if (room.isEnabled()) {
            throw new IllegalStateException("La sala con ID " + roomId + " ya está habilitada.");
        }

        room.setEnabled(true);
        roomRepository.save(room);
    }
}
