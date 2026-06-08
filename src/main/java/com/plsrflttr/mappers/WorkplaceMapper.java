package com.plsrflttr.mappers;

import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.models.Equipment;
import com.plsrflttr.models.Room;
import com.plsrflttr.models.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkplaceMapper {
    @Mapping(target = "roomId", source = "room.id")
    @Mapping(target = "equipmentIds", source = "equipment")
    WorkplaceDto toDto(Workplace workplace);

    @Mapping(target = "room", source = "roomId")
    @Mapping(target = "equipment", source = "equipmentIds")
    Workplace toEntity(WorkplaceDto dto);

    default Room mapRoom(UUID id) {
        if (id == null) {
            return null;
        }
        Room room = new Room();
        room.setId(id);
        return room;
    }

    default Set<UUID> mapEquipmentSet(Set<Equipment> equipment) {
        if (equipment == null) {
            return Collections.emptySet();
        }
        return equipment.stream()
                .map(Equipment::getId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
    }


}
