package com.plsrflttr.mappers;

import com.plsrflttr.dto.FloorDto;
import com.plsrflttr.models.Building;
import com.plsrflttr.models.Floor;
import com.plsrflttr.models.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FloorMapper {
    @Mapping(target = "buildingId", source = "building.id")
    @Mapping(target = "roomIds", source = "rooms")
    FloorDto toDto(Floor floor);

    @Mapping(target = "building", source = "buildingId")
    @Mapping(target = "rooms", ignore = true)
    Floor toEntity(FloorDto dto);

    default Building mapBuilding(UUID id) {
        if (id == null) {
            return null;
        }
        Building building = new Building();
        building.setId(id);
        return building;
    }

    default List<UUID> mapRooms(List<Room> rooms) {
        if (rooms == null) {
            return Collections.emptyList();
        }
        return rooms.stream()
                .map(Room::getId)
                .filter(Objects::nonNull)
                .toList();
    }
}

