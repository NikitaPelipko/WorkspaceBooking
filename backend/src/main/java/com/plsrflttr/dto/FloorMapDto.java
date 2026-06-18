package com.plsrflttr.dto;

import com.plsrflttr.models.BookingMode;
import com.plsrflttr.models.RoomType;
import com.plsrflttr.models.WorkplaceStatus;
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
public class FloorMapDto {

    private UUID floorId;
    private String floorName;
    private Integer floorNumber;
    private String svgUrl;

    private List<RoomMapItem> rooms;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RoomMapItem {
        private UUID id;
        private String name;
        private RoomType type;
        private BookingMode bookingMode;
        private Integer capacity;

        private String svgElementId;

        private boolean availableNow;

        private List<WorkplaceMapItem> workplaces;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class WorkplaceMapItem {
        private UUID id;
        private String code;

        private String svgElementId;

        private WorkplaceStatus status;

        private boolean availableNow;
    }
}