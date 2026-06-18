package com.plsrflttr.mappers;

import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.models.Room;
import com.plsrflttr.models.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE, uses = {EquipmentMapper.class})
public interface WorkplaceMapper {

    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "equipment", source = "equipment")
    WorkplaceDto toDto(Workplace workplace);

    default Room mapRoom(UUID id) {
        if (id == null) return null;
        Room room = new Room();
        room.setId(id);
        return room;
    }
}