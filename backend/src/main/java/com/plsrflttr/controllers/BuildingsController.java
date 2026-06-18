package com.plsrflttr.controllers;

import com.plsrflttr.dto.BuildingDto;
import com.plsrflttr.dto.FloorDto;
import com.plsrflttr.services.BuildingsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/buildings")
@RequiredArgsConstructor
public class BuildingsController {
    private final BuildingsService buildingsService;

    @GetMapping
    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.getAllBuildings());
    }

    @GetMapping("/{buildingId}")
    public ResponseEntity<BuildingDto> getBuildingById(@PathVariable UUID buildingId) {
        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.getBuildingById(buildingId));
    }

    @GetMapping("/{buildingId}/floors")
    public ResponseEntity<List<FloorDto>> getFloorsByBuildingId(@PathVariable UUID buildingId) {
        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.getFloorsByBuildingId(buildingId));
    }

    @GetMapping("/{buildingId}/floors/{floorNumber}")
    public ResponseEntity<FloorDto>  getFloorByFloorNumber(@PathVariable UUID buildingId, @PathVariable int floorNumber) {
        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.getFloorByFloorNumber(buildingId, floorNumber));
    }

}
