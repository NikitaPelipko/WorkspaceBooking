package com.plsrflttr.controllers;

import com.plsrflttr.dto.RoomDto;
import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.services.RoomsService;
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
@RequiredArgsConstructor
@RequestMapping("/api/rooms")
public class RoomsController {
    private final RoomsService roomsService;

    @GetMapping("/{roomId}")
    public ResponseEntity<RoomDto> getRoom(@PathVariable UUID roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getRoom(roomId));
    }

    @GetMapping("/{roomId}/workplaces")
    public ResponseEntity<List<WorkplaceDto>> getWorkplaces(@PathVariable UUID roomId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getWorkplaces(roomId));
    }

}
