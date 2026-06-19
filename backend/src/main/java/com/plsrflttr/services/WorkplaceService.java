package com.plsrflttr.services;

import com.plsrflttr.dto.EquipmentDto;
import com.plsrflttr.dto.WorkplaceDto;
import com.plsrflttr.mappers.EquipmentMapper;
import com.plsrflttr.mappers.WorkplaceMapper;
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
public class WorkplaceService {
    private final WorkplaceRepository workplaceRepository;
    private final RoomRepository roomRepository;
    private final WorkplaceMapper workplaceMapper;
    private final EquipmentMapper equipmentMapper;

    public List<WorkplaceDto> getWorkplaces(UUID roomId) {
        if (roomId != null) {
            roomRepository.findById(roomId)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
            return workplaceRepository.findByRoomId(roomId).stream()
                    .map(workplaceMapper::toDto)
                    .toList();
        }
        return workplaceRepository.findAll().stream()
                .map(workplaceMapper::toDto)
                .toList();
    }

    public WorkplaceDto getWorkplace(UUID id) {
        return workplaceRepository.findById(id)
                .map(workplaceMapper::toDto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));
    }

    public List<EquipmentDto> getEquipments(UUID workplace_id){
        return workplaceRepository.findById(workplace_id)
                .map(workplace -> workplace.getEquipment().stream()
                        .map(equipmentMapper::toDto)
                        .toList())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));
    }
}
