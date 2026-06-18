package com.plsrflttr.services;

import com.plsrflttr.dto.BuildingDto;
import com.plsrflttr.dto.FloorDto;
import com.plsrflttr.mappers.BuildingMapper;
import com.plsrflttr.mappers.FloorMapper;
import com.plsrflttr.repositories.BuildingRepository;
import com.plsrflttr.repositories.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BuildingsService {
    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;
    private final FloorRepository floorRepository;
    private final FloorMapper floorMapper;

    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toDto).toList();
    }

    public BuildingDto getBuildingById(UUID id) {
        return buildingRepository.findById(id)
                .map(buildingMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
    }

    public List<FloorDto> getFloorsByBuildingId(UUID id) {
        buildingRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        return floorRepository.findByBuildingId(id).stream()
                .map(floorMapper::toDto).toList();
    }

    public FloorDto getFloorByFloorNumber(UUID building_id, int floor_number) {
        return floorRepository.findByBuildingIdAndFloorNumber(building_id, floor_number)
                .map(floorMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));
    }

}
