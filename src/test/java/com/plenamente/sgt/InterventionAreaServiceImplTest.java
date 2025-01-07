package com.plenamente.sgt;

import com.plenamente.sgt.domain.dto.InterventionAreaDto.DisabledInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.dto.InterventionAreaDto.UpdateInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import com.plenamente.sgt.infra.repository.InterventionAreaRepository;
import com.plenamente.sgt.mapper.InterventionAreaMapper;
import com.plenamente.sgt.service.impl.InterventionAreaServiceImpl;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest(properties = "spring.profiles.active=test")
public class InterventionAreaServiceImplTest {

    @MockBean
    private InterventionAreaRepository interventionAreaRepository;

    @MockBean
    private InterventionAreaMapper interventionAreaMapper;

    @Autowired
    private InterventionAreaServiceImpl interventionAreaService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createAreaForIntervention_createsAndReturnsInterventionArea() {
        // Arrange
        InterventionArea interventionArea = new InterventionArea();
        interventionArea.setName("Test Area");
        interventionArea.setDescription("Test Description");
        when(interventionAreaRepository.save(any(InterventionArea.class))).thenReturn(interventionArea);

        // Act
        InterventionArea result = interventionAreaService.createAreaForIntervention("Test Area", "Test Description");

        // Assert
        assertNotNull(result);
        assertEquals("Test Area", result.getName());
        assertEquals("Test Description", result.getDescription());
        assertTrue(result.isEnabled());
    }

    @Test
    void updateInterventionArea_updatesAndReturnsInterventionArea() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Old Name");
        existingArea.setDescription("Old Description");
        existingArea.setEnabled(true);

        UpdateInterventionArea updateDto = new UpdateInterventionArea("New Name", "New Description", false);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(interventionAreaRepository.save(any(InterventionArea.class))).thenReturn(existingArea);

        // Act
        InterventionArea result = interventionAreaService.updateInterventionArea(1L, updateDto);

        // Assert
        assertNotNull(result);
        assertEquals("New Name", result.getName());
        assertEquals("New Description", result.getDescription());
        assertFalse(result.isEnabled());
    }

    @Test
    void updateInterventionArea_throwsEntityNotFoundException() {
        // Arrange
        UpdateInterventionArea updateDto = new UpdateInterventionArea("New Name", "New Description", false);
        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> interventionAreaService.updateInterventionArea(1L, updateDto));
    }

    @Test
    void getAllInterventionAreas_returnsListOfEnabledInterventionAreas() {
        // Arrange
        InterventionArea area1 = new InterventionArea();
        area1.setIdInterventionArea(1L);
        area1.setName("Area 1");
        area1.setDescription("Description 1");
        area1.setEnabled(true);

        InterventionArea area2 = new InterventionArea();
        area2.setIdInterventionArea(2L);
        area2.setName("Area 2");
        area2.setDescription("Description 2");
        area2.setEnabled(true);

        when(interventionAreaRepository.findByEnabledTrue()).thenReturn(List.of(area1, area2));
        when(interventionAreaMapper.toDTO(any(InterventionArea.class))).thenReturn(
                new ListInterventionArea(1L, "Area 1", "Description 1", true),
                new ListInterventionArea(2L, "Area 2", "Description 2", true)
        );

        // Act
        List<ListInterventionArea> result = interventionAreaService.getAllInterventionAreas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }

    @Test
    void deleteInterventionArea_disablesAndReturnsInterventionArea() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Test Area");
        existingArea.setDescription("Test Description");
        existingArea.setEnabled(true);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(interventionAreaRepository.save(any(InterventionArea.class))).thenReturn(existingArea);

        // Act
        InterventionArea result = interventionAreaService.deleteInterventionArea(1L);

        // Assert
        assertNotNull(result);
        assertFalse(result.isEnabled());
    }

    @Test
    void deleteInterventionArea_throwsEntityNotFoundException() {
        // Arrange
        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> interventionAreaService.deleteInterventionArea(1L));
    }

    @Test
    void enableInterventionArea_enablesAndReturnsInterventionArea() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Test Area");
        existingArea.setDescription("Test Description");
        existingArea.setEnabled(false);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(interventionAreaRepository.save(any(InterventionArea.class))).thenReturn(existingArea);

        // Act
        interventionAreaService.enableInterventionArea(1L);

        // Assert
        assertTrue(existingArea.isEnabled());
    }

    @Test
    void enableInterventionArea_throwsEntityNotFoundException() {
        // Arrange
        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> interventionAreaService.enableInterventionArea(1L));
    }

    @Test
    void enableInterventionArea_throwsIllegalArgumentException() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Test Area");
        existingArea.setDescription("Test Description");
        existingArea.setEnabled(true);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> interventionAreaService.enableInterventionArea(1L));
    }

    @Test
    void disableInterventionArea_disablesAndReturnsInterventionArea() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Test Area");
        existingArea.setDescription("Test Description");
        existingArea.setEnabled(true);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));
        when(interventionAreaRepository.save(any(InterventionArea.class))).thenReturn(existingArea);

        // Act
        interventionAreaService.disableInterventionArea(1L);

        // Assert
        assertFalse(existingArea.isEnabled());
    }

    @Test
    void disableInterventionArea_throwsEntityNotFoundException() {
        // Arrange
        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class, () -> interventionAreaService.disableInterventionArea(1L));
    }

    @Test
    void disableInterventionArea_throwsIllegalArgumentException() {
        // Arrange
        InterventionArea existingArea = new InterventionArea();
        existingArea.setIdInterventionArea(1L);
        existingArea.setName("Test Area");
        existingArea.setDescription("Test Description");
        existingArea.setEnabled(false);

        when(interventionAreaRepository.findById(1L)).thenReturn(Optional.of(existingArea));

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> interventionAreaService.disableInterventionArea(1L));
    }

    @Test
    void getDisabledInterventionAreas_returnsListOfDisabledInterventionAreas() {
        // Arrange
        InterventionArea area1 = new InterventionArea();
        area1.setIdInterventionArea(1L);
        area1.setName("Area 1");
        area1.setDescription("Description 1");
        area1.setEnabled(false);

        InterventionArea area2 = new InterventionArea();
        area2.setIdInterventionArea(2L);
        area2.setName("Area 2");
        area2.setDescription("Description 2");
        area2.setEnabled(false);

        when(interventionAreaRepository.findByEnabledFalse()).thenReturn(List.of(area1, area2));

        // Act
        List<DisabledInterventionArea> result = interventionAreaService.getDisabledInterventionAreas();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
    }
}