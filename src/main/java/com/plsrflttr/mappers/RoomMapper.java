package com.plsrflttr.mappers;

import com.plsrflttr.dto.RoomDto;
import com.plsrflttr.models.Floor;
import com.plsrflttr.models.Room;
import com.plsrflttr.models.Workplace;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoomMapper {
    @Mapping(target = "floorId", source = "floor.id")
    @Mapping(target = "workplaceIds", source = "workplaces")
    RoomDto toDto(Room room);

    @Mapping(target = "floor", source = "floorId")
    @Mapping(target = "workplaces", ignore = true)
    Room toEntity(RoomDto dto);

    default Floor mapFloor(UUID id) {
        if (id == null) {
            return null;
        }
        Floor floor = new Floor();
        floor.setId(id);
        return floor;
    }

    default List<UUID> mapWorkplaces(List<Workplace> workplaces) {
        if (workplaces == null) {
            return Collections.emptyList();
        }
        return workplaces.stream()
                .map(Workplace::getId)
                .filter(Objects::nonNull)
                .toList();
    }
}

