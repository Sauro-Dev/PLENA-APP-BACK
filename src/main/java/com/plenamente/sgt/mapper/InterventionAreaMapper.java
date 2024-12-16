package com.plenamente.sgt.mapper;


import com.plenamente.sgt.domain.dto.InterventionAreaDto.ListInterventionArea;
import com.plenamente.sgt.domain.entity.InterventionArea;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.stereotype.Component;

@Component
public class InterventionAreaMapper {
    private final ModelMapper modelMapper;

    public InterventionAreaMapper(ModelMapper modelMapper) {
        this.modelMapper = modelMapper;
    }

    public ListInterventionArea toDTO(InterventionArea interventionArea) {
        ModelMapper modelMapper = new ModelMapper();

        TypeMap<InterventionArea, ListInterventionArea> propertyMapper = modelMapper.createTypeMap(InterventionArea.class, ListInterventionArea.class);
        propertyMapper.setConverter(context ->{
            InterventionArea source = context.getSource();
            return new ListInterventionArea(
                    source.getIdInterventionArea(),
                    source.getName(),
                    source.getDescription()
            );
        });
        return modelMapper.map(interventionArea, ListInterventionArea.class);
    }
}
