package com.plenamente.sgt.web.controller;

import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.service.RoomService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/rooms")
@RequiredArgsConstructor
public class RoomController {

    @Autowired
    private RoomService roomService;

    @PostMapping("/register")
    public ResponseEntity<Room> registerRoom(@RequestBody Room room) {
        Room newRoom = roomService.registerRoom(room);
        return ResponseEntity.ok(newRoom);
    }

    @GetMapping("/all")
    public ResponseEntity<List<Room>> listRooms() {
        List<Room> rooms = roomService.listRooms();
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/therapeutic")
    public ResponseEntity<List<Room>> listRoomsByIsTherapeutic(@RequestParam boolean isTherapeutic) {
        List<Room> rooms = roomService.listRoomsByIsTherapeutic(isTherapeutic);
        return ResponseEntity.ok(rooms);
    }

    @GetMapping("/{roomId}/materials")
    public ResponseEntity<List<Material>> getMaterialsByRoom(@PathVariable Long roomId) {
        List<Material> materials = roomService.getMaterialsByRoom(roomId);
        return ResponseEntity.ok(materials);
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<Room> getRoomById(@PathVariable Long roomId) {
        Room room = roomService.getRoomById(roomId);
        return ResponseEntity.ok(room);
    }

    @PutMapping("/update/{roomId}")
    public ResponseEntity<Room> updateRoom(@PathVariable Long roomId, @RequestBody Room roomUpdated) {
        Room room = roomService.updateRoom(roomId,roomUpdated);
        return ResponseEntity.ok(room);
    }

    @DeleteMapping("/delete/{roomId}")
    public ResponseEntity<String> deleteRoom(@PathVariable Long roomId) {
        roomService.deleteRoom(roomId);
        String message = "La sala con ID " + roomId + " fue eliminada exitosamente.";
        return ResponseEntity.ok(message);
    }
}
