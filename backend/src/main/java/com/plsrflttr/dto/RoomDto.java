package com.plsrflttr.dto;

import com.plsrflttr.models.RoomType;
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
public class RoomDto {
    private UUID id;
    private UUID floorId;
    private String name;
    private RoomType type;
    private Integer capacity;
    private String svgElementId;
    private List<UUID> workplaceIds;
}

