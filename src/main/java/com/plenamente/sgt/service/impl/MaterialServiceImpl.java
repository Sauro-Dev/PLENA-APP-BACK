package com.plenamente.sgt.service.impl;

import com.plenamente.sgt.domain.dto.MaterialDto.ListMaterial;
import com.plenamente.sgt.domain.dto.MaterialDto.RegisterMaterial;
import com.plenamente.sgt.domain.entity.Material;
import com.plenamente.sgt.domain.entity.Room;
import com.plenamente.sgt.infra.repository.MaterialAreaRepository;
import com.plenamente.sgt.infra.repository.MaterialRepository;
import com.plenamente.sgt.infra.repository.RoomRepository;
import com.plenamente.sgt.mapper.MaterialMapper;
import com.plenamente.sgt.service.MaterialService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final RoomRepository roomRepository;
    private final MaterialMapper materialMapper;
    private final MaterialAreaRepository areaRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private final CacheManager cacheManager;
    private static final String MATERIALS_CACHE_KEY = "materials";

    @Override
    @Transactional
    @CachePut("materials")
    public Material registerMaterial(RegisterMaterial dto) {
        Material material = new Material();

        String generatedId = generateNextMaterialId();
        material.setIdMaterial(generatedId);
        material.setNombre(dto.nombre());
        material.setDescripcion(dto.descripcion());
        material.setStock(dto.stock());
        material.setEsCompleto(dto.esCompleto());
        material.setEsSoporte(dto.esSoporte());
        material.setEstado(dto.estado());

        Material savedMaterial = materialRepository.save(material);

        // Limpiar la cache de materiales y actualizarla
        cacheManager.getCache("materials").clear();
        updateMaterialsCache();

        return savedMaterial;
    }

    @Override
    public List<RegisterMaterial> getAllMaterials() {
        log.info("Intentando obtener materiales de la caché");
        // Intentar obtener de la caché
        @SuppressWarnings("unchecked")
        List<RegisterMaterial> cachedMaterials = (List<RegisterMaterial>) redisTemplate
                .opsForValue()
                .get(MATERIALS_CACHE_KEY);

        if (cachedMaterials == null) {
            log.info("No se encontraron materiales en caché, obteniendo de la base de datos");
            // Si no está en caché, obtener de la base de datos
            List<Material> materials = materialRepository.findAll();
            List<RegisterMaterial> materialDTOs = materials.stream()
                    .map(materialMapper::toDTO)
                    .toList();

            log.info("Guardando {} materiales en caché", materialDTOs.size());
            // Guardar en caché
            redisTemplate.opsForValue().set(MATERIALS_CACHE_KEY, materialDTOs);
            return materialDTOs;
        }

        log.info("Retornando {} materiales desde caché", cachedMaterials.size());
        return cachedMaterials;
    }

    private void updateMaterialsCache() {
        log.info("Actualizando caché de materiales");
        List<Material> materials = materialRepository.findAll();
        List<RegisterMaterial> materialDTOs = materials.stream()
                .map(materialMapper::toDTO)
                .toList();
        redisTemplate.opsForValue().set(MATERIALS_CACHE_KEY, materialDTOs);
        log.info("Caché actualizada con {} materiales", materialDTOs.size());
    }

    public void clearMaterialsCache() {
        redisTemplate.delete(MATERIALS_CACHE_KEY);
        cacheManager.getCache("materials").clear();
    }

    @Override
    public ListMaterial getMaterialById(String id) {
        Material material = materialRepository.findById(id).orElseThrow(EntityNotFoundException::new);
        return materialMapper.ListDTO(material);
    }

    @Override
    public Material updateMaterial(String id, RegisterMaterial updatedMaterial) {
        Material existingMaterial = materialRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + id));

        existingMaterial.setNombre(updatedMaterial.nombre());
        existingMaterial.setDescripcion(updatedMaterial.descripcion());
        existingMaterial.setStock(updatedMaterial.stock());
        existingMaterial.setEsCompleto(updatedMaterial.esCompleto());
        existingMaterial.setEsSoporte(updatedMaterial.esSoporte());
        existingMaterial.setEstado(updatedMaterial.estado());

        return materialRepository.save(existingMaterial);
    }
    @Transactional
    public void deleteMaterial(String materialId){
        areaRepository.deleteByMaterial_IdMaterial(materialId);
        materialRepository.deleteById(materialId);
    }
    public String generateNextMaterialId() {
        // Obtener el último ID insertado
        Optional<Material> lastMaterialOpt = materialRepository.findTopByOrderByIdMaterialDesc();

        String lastMaterialId = lastMaterialOpt.map(Material::getIdMaterial).orElse("A000");

        // Extraer la parte alfabética y la parte numérica
        String alphaPart = lastMaterialId.substring(0, 1);  // Letra
        int numericPart = Integer.parseInt(lastMaterialId.substring(1));  // Números

        // Incrementar la parte numérica
        numericPart++;

        // Si la parte numérica llega a 1000, reiniciarla y pasar a la siguiente letra
        if (numericPart > 999) {
            numericPart = 1;  // Reiniciar la parte numérica
            alphaPart = incrementAlphaPart(alphaPart);  // Incrementar la letra
        }

        // Formatear el nuevo ID (A001, B001, etc.)
        return String.format("%s%03d", alphaPart, numericPart);
    }

    @Override
    public String incrementAlphaPart(String alphaPart) {
        // Convertir la letra a su valor ASCII y sumar 1
        char nextChar = (char) (alphaPart.charAt(0) + 1);

        // Si se pasa de 'Z', no permite continuar (puedes cambiar la lógica si es necesario)
        if (nextChar > 'Z') {
            throw new IllegalStateException("Se ha alcanzado el límite máximo de IDs: Z999");
        }

        return String.valueOf(nextChar);
    }

    @Override
    public Material assignMaterialToRoom(String materialId, Long roomId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + materialId));

        Room room = roomRepository.findByIdRoomAndEnabledTrue(roomId)
                .orElseThrow(() -> new IllegalStateException("Sala no encontrada o está deshabilitada (ID: " + roomId + ")."));

        if (!room.isEnabled()) {
            throw new IllegalStateException("No se puede asignar materiales a una sala deshabilitada (ID: " + roomId + ").");
        }

        material.setRoom(room);
        return materialRepository.save(material);
    }

    @Override
    public Material unassignMaterialFromRoom(String materialId) {
        Material material = materialRepository.findById(materialId)
                .orElseThrow(() -> new EntityNotFoundException("Material no encontrado con id: " + materialId));

        material.setRoom(null);
        return materialRepository.save(material);
    }

    @Override
    public List<RegisterMaterial> getUnassignedMaterials() {
        List<Material> materials = materialRepository.findByRoomIsNull();
        return materials.stream()
                .map(materialMapper::toDTO)
                .toList();
    }


}
