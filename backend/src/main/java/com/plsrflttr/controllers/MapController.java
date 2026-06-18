package com.plsrflttr.controllers;

import com.plsrflttr.dto.FloorMapDto;
import com.plsrflttr.services.FloorMapService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/map")
@RequiredArgsConstructor
public class MapController {

    private final FloorMapService floorMapService;

    @GetMapping("/floors/{floorId}")
    public ResponseEntity<FloorMapDto> getFloorMap(
            @PathVariable UUID floorId,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime from,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime to
    ) {
        return ResponseEntity.ok(floorMapService.getFloorMap(floorId, from, to));
    }
}