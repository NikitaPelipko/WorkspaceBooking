package com.plsrflttr.services;

import com.plsrflttr.dto.FloorMapDto;
import com.plsrflttr.models.*;
import com.plsrflttr.repositories.BookingRepository;
import com.plsrflttr.repositories.FloorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class FloorMapService {

    private final FloorRepository floorRepository;
    private final BookingRepository bookingRepository;
    private final MinioService minioService;

    public FloorMapDto getFloorMap(
            UUID floorId,
            LocalDateTime from,
            LocalDateTime to
    ) {

        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() ->
                        new ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Floor not found"
                        )
                );

        LocalDateTime checkFrom =
                from != null ? from : LocalDateTime.now();

        LocalDateTime checkTo =
                to != null ? to : checkFrom.plusMinutes(1);

        if (!checkFrom.isBefore(checkTo)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Start time must be before end time"
            );
        }

        Set<UUID> targetIds = new HashSet<>();

        floor.getRooms().forEach(room -> {

            targetIds.add(room.getId());

            room.getWorkplaces()
                    .forEach(wp -> targetIds.add(wp.getId()));
        });

        Set<UUID> busyTargets =
                bookingRepository.findBusyTargets(
                                targetIds,
                                checkFrom,
                                checkTo
                        )
                        .stream()
                        .map(Booking::getTargetId)
                        .collect(Collectors.toSet());

        List<FloorMapDto.RoomMapItem> roomItems =
                floor.getRooms()
                        .stream()
                        .map(room -> buildRoomItem(
                                room,
                                busyTargets
                        ))
                        .toList();

        FloorMapDto dto = new FloorMapDto();

        dto.setFloorId(floor.getId());
        dto.setFloorName(floor.getName());
        dto.setFloorNumber(floor.getFloorNumber());
        dto.setSvgUrl(minioService.getPresignedUrl(
                floor.getSvgObjectKey()
        ));
        dto.setRooms(roomItems);

        return dto;
    }


    private FloorMapDto.RoomMapItem buildRoomItem(
            Room room,
            Set<UUID> busyTargets
    ) {

        FloorMapDto.RoomMapItem item =
                new FloorMapDto.RoomMapItem();

        item.setId(room.getId());
        item.setName(room.getName());
        item.setType(room.getType());
        item.setBookingMode(room.getBookingMode());
        item.setCapacity(room.getCapacity());
        item.setSvgElementId(room.getSvgElementId());

        switch (room.getBookingMode()) {

            case ROOM -> {

                item.setAvailableNow(
                        !busyTargets.contains(room.getId())
                );

                item.setWorkplaces(List.of());
            }

            case WORKPLACE -> {

                List<FloorMapDto.WorkplaceMapItem> workplaces =
                        room.getWorkplaces()
                                .stream()
                                .map(wp ->
                                        buildWorkplaceItem(
                                                wp,
                                                busyTargets
                                        ))
                                .toList();

                item.setWorkplaces(workplaces);

                item.setAvailableNow(
                        workplaces.stream()
                                .anyMatch(
                                        FloorMapDto.WorkplaceMapItem::isAvailableNow
                                )
                );
            }
        }

        return item;
    }

    private FloorMapDto.WorkplaceMapItem buildWorkplaceItem(
            Workplace wp,
            Set<UUID> busyTargets
    ) {

        FloorMapDto.WorkplaceMapItem item =
                new FloorMapDto.WorkplaceMapItem();

        item.setId(wp.getId());
        item.setCode(wp.getCode());
        item.setSvgElementId(wp.getSvgElementId());
        item.setStatus(wp.getStatus());

        boolean availableNow =
                wp.getStatus() == WorkplaceStatus.AVAILABLE
                        && !busyTargets.contains(wp.getId());

        item.setAvailableNow(availableNow);

        return item;
    }
}