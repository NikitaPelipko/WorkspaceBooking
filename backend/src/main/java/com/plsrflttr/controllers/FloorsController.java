package com.plsrflttr.controllers;

import com.plsrflttr.dto.RoomDto;
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
@RequestMapping("/api/floors")
@RequiredArgsConstructor
public class FloorsController {
    private final RoomsService roomsService;

    @GetMapping("/{floorId}/rooms")
    public ResponseEntity<List<RoomDto>> getRooms(@PathVariable UUID floorId) {
        return ResponseEntity.status(HttpStatus.OK).body(roomsService.getRooms(floorId, null));
    }


}
