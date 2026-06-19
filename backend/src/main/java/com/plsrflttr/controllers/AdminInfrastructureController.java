package com.plsrflttr.controllers;

import com.plsrflttr.dto.BuildingDto;
import com.plsrflttr.dto.EquipmentDto;
import com.plsrflttr.dto.FloorDto;
import com.plsrflttr.dto.RoomDto;
import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminInfrastructureController {

    private final AdminService adminService;

    // ==================== BUILDINGS ====================

    @GetMapping("/buildings")
    public ResponseEntity<List<BuildingDto>> getAllBuildings() {
        return ResponseEntity.ok(adminService.getAllBuildings());
    }

    @PostMapping("/buildings")
    public ResponseEntity<BuildingDto> createBuilding(@RequestBody BuildingDto buildingDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createBuilding(buildingDto));
    }

    @PutMapping("/buildings/{buildingId}")
    public ResponseEntity<BuildingDto> updateBuilding(
            @PathVariable UUID buildingId,
            @RequestBody BuildingDto buildingDto
    ) {
        return ResponseEntity.ok(adminService.updateBuilding(buildingId, buildingDto));
    }

    @DeleteMapping("/buildings/{buildingId}")
    public ResponseEntity<Void> deleteBuilding(@PathVariable UUID buildingId) {
        adminService.deleteBuilding(buildingId);
        return ResponseEntity.noContent().build();
    }

    // ==================== FLOORS ====================

    @GetMapping("/floors")
    public ResponseEntity<List<FloorDto>> getAllFloors() {
        return ResponseEntity.ok(adminService.getAllFloors());
    }

    @PostMapping(value = "/floors")
    public ResponseEntity<FloorDto> createFloor(
            @RequestBody FloorDto floorDto
    ) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createFloor(floorDto));
    }

    @PutMapping(value = "/floors/{floorId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<FloorDto> updateFloor(
            @PathVariable UUID floorId,
            @RequestBody FloorDto floorDto
    ) {
        return ResponseEntity.ok(adminService.updateFloor(floorId, floorDto));
    }

    @DeleteMapping("/floors/{floorId}")
    public ResponseEntity<Void> deleteFloor(@PathVariable UUID floorId) {
        adminService.deleteFloor(floorId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ROOMS ====================

    @GetMapping("/rooms")
    public ResponseEntity<List<RoomDto>> getAllRooms() {
        return ResponseEntity.ok(adminService.getAllRooms());
    }

    @PostMapping("/rooms")
    public ResponseEntity<RoomDto> createRoom(@RequestBody RoomDto roomDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createRoom(roomDto));
    }

    @PutMapping("/rooms/{roomId}")
    public ResponseEntity<RoomDto> updateRoom(
            @PathVariable UUID roomId,
            @RequestBody RoomDto roomDto
    ) {
        return ResponseEntity.ok(adminService.updateRoom(roomId, roomDto));
    }

    @DeleteMapping("/rooms/{roomId}")
    public ResponseEntity<Void> deleteRoom(@PathVariable UUID roomId) {
        adminService.deleteRoom(roomId);
        return ResponseEntity.noContent().build();
    }

    // ==================== WORKPLACES ====================

    @GetMapping("/workplaces")
    public ResponseEntity<List<WorkplaceDto>> getAllWorkplaces() {
        return ResponseEntity.ok(adminService.getAllWorkplaces());
    }

    @PostMapping("/workplaces")
    public ResponseEntity<WorkplaceDto> createWorkplace(@RequestBody WorkplaceDto workplaceDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createWorkplace(workplaceDto));
    }

    @PutMapping("/workplaces/{workplaceId}")
    public ResponseEntity<WorkplaceDto> updateWorkplace(
            @PathVariable UUID workplaceId,
            @RequestBody WorkplaceDto workplaceDto
    ) {
        return ResponseEntity.ok(adminService.updateWorkplace(workplaceId, workplaceDto));
    }

    @DeleteMapping("/workplaces/{workplaceId}")
    public ResponseEntity<Void> deleteWorkplace(@PathVariable UUID workplaceId) {
        adminService.deleteWorkplace(workplaceId);
        return ResponseEntity.noContent().build();
    }

    // ==================== EQUIPMENT ====================

    @GetMapping("/equipment")
    public ResponseEntity<List<EquipmentDto>> getAllEquipment() {
        return ResponseEntity.ok(adminService.getAllEquipment());
    }

    @PostMapping("/equipment")
    public ResponseEntity<EquipmentDto> createEquipment(@RequestBody EquipmentDto equipmentDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(adminService.createEquipment(equipmentDto));
    }

    @PutMapping("/equipment/{equipmentId}")
    public ResponseEntity<EquipmentDto> updateEquipment(
            @PathVariable UUID equipmentId,
            @RequestBody EquipmentDto equipmentDto
    ) {
        return ResponseEntity.ok(adminService.updateEquipment(equipmentId, equipmentDto));
    }

    @DeleteMapping("/equipment/{equipmentId}")
    public ResponseEntity<Void> deleteEquipment(@PathVariable UUID equipmentId) {
        adminService.deleteEquipment(equipmentId);
        return ResponseEntity.noContent().build();
    }
}
