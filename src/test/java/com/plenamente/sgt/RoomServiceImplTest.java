package com.plenamente.sgt;

import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.infra.repository.MaterialRepository;
import com.plenamente.sgt.infra.repository.RoomRepository;
import com.plenamente.sgt.service.impl.RoomServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class RoomServiceImplTest {

    @MockBean
    private RoomRepository roomRepository;

    @MockBean
    private MaterialRepository materialRepository;

    @Autowired
    private RoomServiceImpl roomService;

    @BeforeEach
    void setUp() {
        System.out.println("\n=== Iniciando nuevo test para room ===");
    }

    @Test
    void getMaterialsByRoomReturnsMaterials() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);
        List<Material> materials = List.of(new Material());

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(materialRepository.findByRoom(room)).thenReturn(materials);

        // Act
        List<Material> result = roomService.getMaterialsByRoom(1L);

        // Assert
        assertThat(result).isEqualTo(materials);
    }

    @Test
    void getMaterialsByRoomThrowsEntityNotFoundException() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomService.getMaterialsByRoom(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Room no encontrado con id: 1");
    }

    @Test
    void registerRoomSavesRoom() {
        // Arrange
        Room room = new Room();
        room.setName("Room Name");
        room.setAddress("Room Address");

        when(roomRepository.save(room)).thenReturn(room);

        // Act
        Room result = roomService.registerRoom(room);

        // Assert
        assertThat(result).isEqualTo(room);
    }

    @Test
    void registerRoomThrowsIllegalArgumentExceptionWhenNameIsNull() {
        // Arrange
        Room room = new Room();
        room.setAddress("Room Address");

        // Act & Assert
        assertThatThrownBy(() -> roomService.registerRoom(room))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Name is required");
    }

    @Test
    void registerRoomThrowsIllegalArgumentExceptionWhenAddressIsNull() {
        // Arrange
        Room room = new Room();
        room.setName("Room Name");

        // Act & Assert
        assertThatThrownBy(() -> roomService.registerRoom(room))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Address is required");
    }

    @Test
    void listRoomsReturnsEnabledRooms() {
        // Arrange
        List<Room> rooms = List.of(new Room());

        when(roomRepository.findByEnabledTrue()).thenReturn(rooms);

        // Act
        List<Room> result = roomService.listRooms();

        // Assert
        assertThat(result).isEqualTo(rooms);
    }

    @Test
    void listRoomsByIsTherapeuticReturnsRooms() {
        // Arrange
        List<Room> rooms = List.of(new Room());

        when(roomRepository.findByIsTherapeuticAndEnabledTrue(true)).thenReturn(rooms);

        // Act
        List<Room> result = roomService.listRoomsByIsTherapeutic(true);

        // Assert
        assertThat(result).isEqualTo(rooms);
    }

    @Test
    void getRoomByIdReturnsRoom() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // Act
        Room result = roomService.getRoomById(1L);

        // Assert
        assertThat(result).isEqualTo(room);
    }

    @Test
    void getRoomByIdThrowsEntityNotFoundException() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomService.getRoomById(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Room no encontrado con id: 1");
    }

    @Test
    void updateRoomUpdatesRoom() {
        // Arrange
        Room existingRoom = new Room();
        existingRoom.setIdRoom(1L);
        Room updatedRoom = new Room();
        updatedRoom.setName("Updated Name");

        when(roomRepository.findById(1L)).thenReturn(Optional.of(existingRoom));
        when(roomRepository.save(existingRoom)).thenReturn(existingRoom);

        // Act
        Room result = roomService.updateRoom(1L, updatedRoom);

        // Assert
        assertThat(result.getName()).isEqualTo("Updated Name");
    }

    @Test
    void updateRoomThrowsEntityNotFoundException() {
        // Arrange
        Room updatedRoom = new Room();
        updatedRoom.setName("Updated Name");

        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomService.updateRoom(1L, updatedRoom))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Room no encontrado con id: 1");
    }

    @Test
    void disableRoomDisablesRoom() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);
        room.setEnabled(true);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // Act
        roomService.disableRoom(1L);

        // Assert
        assertThat(room.isEnabled()).isFalse();
    }

    @Test
    void disableRoomThrowsEntityNotFoundException() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomService.disableRoom(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sala no encontrada con ID: 1");
    }

    @Test
    void disableRoomThrowsIllegalStateExceptionWhenAlreadyDisabled() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);
        room.setEnabled(false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // Act & Assert
        assertThatThrownBy(() -> roomService.disableRoom(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La sala con ID 1 ya está deshabilitada.");
    }

    @Test
    void enableRoomEnablesRoom() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);
        room.setEnabled(false);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));
        when(roomRepository.save(room)).thenReturn(room);

        // Act
        roomService.enableRoom(1L);

        // Assert
        assertThat(room.isEnabled()).isTrue();
    }

    @Test
    void enableRoomThrowsEntityNotFoundException() {
        // Arrange
        when(roomRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> roomService.enableRoom(1L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessage("Sala no encontrada con ID: 1");
    }

    @Test
    void enableRoomThrowsIllegalStateExceptionWhenAlreadyEnabled() {
        // Arrange
        Room room = new Room();
        room.setIdRoom(1L);
        room.setEnabled(true);

        when(roomRepository.findById(1L)).thenReturn(Optional.of(room));

        // Act & Assert
        assertThatThrownBy(() -> roomService.enableRoom(1L))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("La sala con ID 1 ya está habilitada.");
    }
}