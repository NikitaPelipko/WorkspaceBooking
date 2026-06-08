package com.plsrflttr.controllers;

import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.services.WorkplaceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/workplaces")
@RequiredArgsConstructor
public class WorkplaceController {
    private final WorkplaceService workplaceService;

    @GetMapping("/{workplaceId}")
    public ResponseEntity<WorkplaceDto> getWorkplace(@PathVariable("workplaceId") UUID workplaceId){
        return ResponseEntity.status(HttpStatus.OK).body(workplaceService.getWorkplace(workplaceId));
    }

}
