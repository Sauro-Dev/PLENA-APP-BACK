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
        return roomRepository.findAll();
    }

    @Override
    public List<Room> listRoomsByIsTherapeutic(boolean isTherapeutic) {
        return roomRepository.findByIsTherapeutic(isTherapeutic);
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
        // Actualizar los valores con los datos proporcionados
        if (roomUpdated.getName() != null) {
            existingRoom.setName(roomUpdated.getName());
        }
        if (roomUpdated.getAddress() != null) {
            existingRoom.setAddress(roomUpdated.getAddress());
        }
        if (roomUpdated.getIsTherapeutic() != null) {
            existingRoom.setIsTherapeutic(roomUpdated.getIsTherapeutic());
        }

        // Guardar los cambios en la base de datos
        return roomRepository.save(existingRoom);
    }

    @Override
    public String deleteRoom(Long roomId) {
        Room existingRoom = roomRepository.findById(roomId)
                .orElseThrow(() -> new EntityNotFoundException("Room no encontrado con id: " + roomId));
        List<Material> materials = materialRepository.findByRoom(existingRoom);
        for (Material material : materials) {
            material.setRoom(null);  // Desasignar material
            materialRepository.save(material);  // Guardar el cambio
        }
        roomRepository.deleteById(roomId);
        return "La sala con ID " + roomId + " y nombre '" + existingRoom.getName() + "' fue eliminada exitosamente.";
    }
}