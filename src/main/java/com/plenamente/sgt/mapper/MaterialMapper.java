package com.plenamente.sgt.mapper;

import com.plenamente.sgt.domain.dto.MaterialDto.ListMaterial;
import com.plenamente.sgt.domain.dto.MaterialDto.RegisterMaterial;
import com.plenamente.sgt.domain.entity.Material;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class MaterialMapper {
    private final ModelMapper modelMapper;

    public MaterialMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public RegisterMaterial toDTO(Material Material) {
        ModelMapper modelMapper = new ModelMapper();

        // Configuración de mapeo entre Material y RegisterMaterial
        TypeMap<Material, RegisterMaterial> propertyMapper = modelMapper.createTypeMap(Material.class, RegisterMaterial.class);
        propertyMapper.setConverter(context -> {
            Material source = context.getSource();
            return new RegisterMaterial(
                    source.getIdMaterial(),
                    source.getNombre(),
                    source.getDescripcion(),
                    source.getStock(),
                    source.isEsCompleto(),
                    source.isEsSoporte(),
                    source.getEstado()
            );
        });
        return modelMapper.map(Material, RegisterMaterial.class);
    }

    public Material toEntity(RegisterMaterial registerMaterial) {
        return modelMapper.map(registerMaterial, Material.class);
    }
    public ListMaterial ListDTO(Material material){
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<Material, ListMaterial> propertyMapper = modelMapper.createTypeMap(Material.class, ListMaterial.class);
        propertyMapper.setConverter(context -> {
            Material source = context.getSource();
            Long roomId = (source.getRoom() != null) ? source.getRoom().getIdRoom() : null;
            return new ListMaterial(
                    source.getIdMaterial(),
                    source.getNombre(),
                    source.getDescripcion(),
                    source.getStock(),
                    source.getEstado(),
                    source.getFechaAlta(),
                    source.isEsCompleto(),
                    source.isEsSoporte(),
                    roomId
            );
        });
        return modelMapper.map(material, ListMaterial.class);
    }
}
