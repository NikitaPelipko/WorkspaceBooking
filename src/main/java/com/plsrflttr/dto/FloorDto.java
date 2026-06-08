package com.plsrflttr.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FloorDto {
    private UUID id;
    private UUID buildingId;
    private String name;
    private Integer floorNumber;
    private String svgUrl;
    private List<UUID> roomIds;
}

