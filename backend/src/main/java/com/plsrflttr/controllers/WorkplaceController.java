package com.plsrflttr.controllers;

import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.services.WorkplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/workplaces")
@RequiredArgsConstructor
public class WorkplaceController {
    private final WorkplaceService workplaceService;

    @GetMapping
    public ResponseEntity<List<WorkplaceDto>> getWorkplaces(
            @RequestParam(required = false) UUID roomId
    ) {
        return ResponseEntity.status(HttpStatus.OK).body(workplaceService.getWorkplaces(roomId));
    }

    @GetMapping("/{workplaceId}")
    public ResponseEntity<WorkplaceDto> getWorkplace(@PathVariable("workplaceId") UUID workplaceId){
        return ResponseEntity.status(HttpStatus.OK).body(workplaceService.getWorkplace(workplaceId));
    }

}
