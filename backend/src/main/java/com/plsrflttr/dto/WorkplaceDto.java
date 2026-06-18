package com.plsrflttr.dto;

import com.plsrflttr.models.WorkplaceStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class WorkplaceDto {
    private UUID id;
    private UUID roomId;
    private String code;
    private String svgElementId;
    private WorkplaceStatus status;
    private Set<EquipmentDto> equipment;
}

