package com.plsrflttr.services;

import com.plsrflttr.dto.RoomDto;
import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.mappers.RoomMapper;
import com.plsrflttr.mappers.WorkplaceMapper;
import com.plsrflttr.models.RoomType;
import com.plsrflttr.repositories.FloorRepository;
import com.plsrflttr.repositories.RoomRepository;
import com.plsrflttr.repositories.WorkplaceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RoomsService {
    private final RoomRepository roomRepository;
    private final FloorRepository floorRepository;
    private final RoomMapper roomMapper;
    private final WorkplaceRepository workplaceRepository;
    private final WorkplaceMapper workplaceMapper;

    public List<RoomDto> getRooms(UUID floorUuid, RoomType type) {
        if (floorUuid != null && type != null) {
            floorRepository.findById(floorUuid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));
            return roomRepository.findByFloorIdAndType(floorUuid, type)
                    .stream().map(roomMapper::toDto).toList();
        }
        if (floorUuid != null) {
            floorRepository.findById(floorUuid)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));
            return roomRepository.findByFloorId(floorUuid)
                    .stream().map(roomMapper::toDto).toList();
        }
        if (type != null) {
            return roomRepository.findAll().stream()
                    .filter(r -> r.getType() == type)
                    .map(roomMapper::toDto)
                    .toList();
        }
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto)
                .toList();
    }

    public RoomDto getRoom(UUID room_id) {
        return roomRepository.findById(room_id)
                .map(roomMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
    }

    public List<WorkplaceDto> getWorkplaces(UUID room_id) {
        roomRepository.findById(room_id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        return workplaceRepository.findByRoomId(room_id).stream()
                .map(workplaceMapper::toDto).toList();
    }
}
