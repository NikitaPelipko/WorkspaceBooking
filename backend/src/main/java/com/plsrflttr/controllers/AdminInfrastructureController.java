//package com.plsrflttr.controllers;
//
//import com.plsrflttr.dto.BuildingDto;
//import com.plsrflttr.dto.FloorDto;
//import com.plsrflttr.models.Building;
//import com.plsrflttr.services.BuildingsService;
//import com.plsrflttr.services.FloorMapService;
//import com.plsrflttr.services.RoomsService;
//import com.plsrflttr.services.WorkplaceService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RequiredArgsConstructor
//@RestController()
//@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
//public class AdminInfrastructureController {
//    private final BuildingsService buildingsService;
//    private final FloorMapService floorMapService;
//    private final RoomsService roomsService;
//    private final WorkplaceService workplaceService;
//
//    @GetMapping("/buildings")
//    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
//        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.getAllBuildings());
//    }
//
//    @PostMapping("/buildings")
//    public ResponseEntity<BuildingDto> createBuilding(BuildingDto buildingDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(buildingsService.createBuilding(buildingDto));
//    }
//
//    @PutMapping("/buildings/{buildingId}")
//    public ResponseEntity<BuildingDto> updateBuilding(@PathVariable UUID buildingId, @RequestBody BuildingDto buildingDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(buildingsService.updateBuilding(buildingId,BuildingDto));
//    }
//
//    @DeleteMapping("/buildings/{buildingId}")
//    public ResponseEntity<Void> deleteBuilding(@PathVariable UUID buildingId) {
//        buildingsService.deleteBuilding(buildingId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/floors")
//    public ResponseEntity<List<FloorDto>> getAllFloors() {
//        return ResponseEntity.status(HttpStatus.OK).body(floorMapService.getAllFloors());
//    }
//
//    @PostMapping("/floors")
//    public ResponseEntity<FloorDto> createFloor(@RequestBody FloorDto floorDto) {
//        return ResponseEntity.status(HttpStatus.CREATED).body(floorMapService.createFloor(floorDto));
//    }
//
//    @PutMapping("/floors/{floorId}")
//    public ResponseEntity<FloorDto> updateFloor(@PathVariable UUID floorId, @RequestBody FloorDto floorDto) {
//        return ResponseEntity.status(HttpStatus.OK).body(floorMapService.updateFloor(floorId, floorDto));
//    }
//
//    @DeleteMapping("/floors/{floorId}")
//    public ResponseEntity<Void> deleteFloor(@PathVariable UUID floorId) {
//        floorMapService.deleteFloor
//        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
//    }
//}
