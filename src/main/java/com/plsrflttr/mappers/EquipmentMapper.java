package com.plsrflttr.mappers;

import com.plsrflttr.dto.EquipmentDto;
import com.plsrflttr.models.Equipment;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface EquipmentMapper {
    EquipmentDto toDto(Equipment equipment);

    Equipment toEntity(EquipmentDto dto);
}

