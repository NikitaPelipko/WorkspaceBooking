package com.plsrflttr.mappers;

import com.plsrflttr.dto.BuildingDto;
import com.plsrflttr.models.Building;
import com.plsrflttr.models.Floor;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuildingMapper {
    @Mapping(target = "floorIds", source = "floors")
    BuildingDto toDto(Building building);

    @Mapping(target = "floors", ignore = true)
    Building toEntity(BuildingDto dto);

    default List<UUID> mapFloors(List<Floor> floors) {
        if (floors == null) {
            return Collections.emptyList();
        }
        return floors.stream()
                .map(Floor::getId)
                .filter(Objects::nonNull)
                .toList();
    }
}

