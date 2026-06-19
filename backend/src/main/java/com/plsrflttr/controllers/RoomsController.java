package com.plsrflttr.controllers;

import com.plsrflttr.dto.RoomDto;
import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.models.RoomType;
import com.plsrflttr.services.RoomsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomsController {
    private final RoomsService roomsService;

    @GetMapping
    public ResponseEntity<List<RoomDto>> getRooms(
            @RequestParam(required = false) UUID floorId,
            @RequestParam(required = false) RoomType type
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getRooms(floorId, type));
    }

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable UUID roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getRoom(roomId));
    }

    @GetMapping("/{roomId}/workplaces")
    public ResponseEntity<List<WorkplaceDto>> getWorkplaces(@PathVariable UUID roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getWorkplaces(roomId));
    }

}
