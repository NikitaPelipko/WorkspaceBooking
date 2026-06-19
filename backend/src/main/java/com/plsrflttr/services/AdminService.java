package com.plsrflttr.services;

import com.plsrflttr.dto.*;
import com.plsrflttr.mappers.*;
import com.plsrflttr.models.*;
import com.plsrflttr.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class AdminService {

    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BuildingRepository buildingRepository;
    private final FloorRepository floorRepository;
    private final RoomRepository roomRepository;
    private final WorkplaceRepository workplaceRepository;
    private final EquipmentRepository equipmentRepository;
    private final UserMapper userMapper;
    private final BookingMapper bookingMapper;
    private final BuildingMapper buildingMapper;
    private final FloorMapper floorMapper;
    private final RoomMapper roomMapper;
    private final WorkplaceMapper workplaceMapper;
    private final EquipmentMapper equipmentMapper;
    private final MinioService minioService;

    // ==================== USERS ====================

    @Transactional(readOnly = true)
    public List<UserDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    public void blockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setEnabled(false);
        userRepository.save(user);
    }

    public void unblockUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found"));
        user.setEnabled(true);
        userRepository.save(user);
    }

    // ==================== BOOKINGS ====================

    @Transactional(readOnly = true)
    public List<BookingDto> getAllBookings() {
        return bookingRepository.findAll()
                .stream()
                .map(bookingMapper::toDto)
                .map(this::enrichWithTargetName)
                .toList();
    }

    public BookingDto updateBookingStatus(UUID bookingId, BookingStatus status) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found"));
        booking.setStatus(status);
        return enrichWithTargetName(bookingMapper.toDto(bookingRepository.save(booking)));
    }

    // ==================== BUILDINGS ====================

    @Transactional(readOnly = true)
    public List<BuildingDto> getAllBuildings() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toDto).toList();
    }

    public BuildingDto createBuilding(BuildingDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Building name is required");
        }
        Building building = buildingMapper.toEntity(dto);
        Building saved = buildingRepository.save(building);
        return buildingMapper.toDto(saved);
    }

    public BuildingDto updateBuilding(UUID buildingId, BuildingDto dto) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        if (dto.getName() != null) building.setName(dto.getName());
        if (dto.getAddress() != null) building.setAddress(dto.getAddress());
        return buildingMapper.toDto(buildingRepository.save(building));
    }

    public void deleteBuilding(UUID buildingId) {
        Building building = buildingRepository.findById(buildingId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));
        // Delete associated floor SVGs from MinIO
        if (building.getFloors() != null) {
            for (Floor floor : building.getFloors()) {
                if (floor.getSvgObjectKey() != null) {
                    try { minioService.delete(floor.getSvgObjectKey()); } catch (Exception ignored) {}
                }
            }
        }
        buildingRepository.delete(building);
    }

    // ==================== FLOORS ====================

    @Transactional(readOnly = true)
    public List<FloorDto> getAllFloors() {
        return floorRepository.findAll().stream()
                .map(floorMapper::toDto).toList();
    }

    public FloorDto createFloor(FloorDto dto, MultipartFile svgFile) {
        if (dto.getBuildingId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Building ID is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Floor name is required");
        }
        Building building = buildingRepository.findById(dto.getBuildingId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Building not found"));

        Floor floor = floorMapper.toEntity(dto);
        floor.setBuilding(building);

        // Upload SVG if provided
        if (svgFile != null && !svgFile.isEmpty()) {
            String objectKey = "floors/" + UUID.randomUUID() + ".svg";
            minioService.uploadSvg(svgFile, objectKey);
            floor.setSvgObjectKey(objectKey);
        }

        Floor saved = floorRepository.save(floor);
        return floorMapper.toDto(saved);
    }

    public FloorDto updateFloor(UUID floorId, FloorDto dto, MultipartFile svgFile) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));

        if (dto.getName() != null) floor.setName(dto.getName());
        if (dto.getFloorNumber() != null) floor.setFloorNumber(dto.getFloorNumber());

        if (svgFile != null && !svgFile.isEmpty()) {
            if (floor.getSvgObjectKey() != null) {
                try { minioService.delete(floor.getSvgObjectKey()); } catch (Exception ignored) {}
            }
            String objectKey = "floors/" + UUID.randomUUID() + ".svg";
            minioService.uploadSvg(svgFile, objectKey);
            floor.setSvgObjectKey(objectKey);
        }

        return floorMapper.toDto(floorRepository.save(floor));
    }

    public void deleteFloor(UUID floorId) {
        Floor floor = floorRepository.findById(floorId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));
        if (floor.getSvgObjectKey() != null) {
            try { minioService.delete(floor.getSvgObjectKey()); } catch (Exception ignored) {}
        }
        floorRepository.delete(floor);
    }

    // ==================== ROOMS ====================

    @Transactional(readOnly = true)
    public List<RoomDto> getAllRooms() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toDto).toList();
    }

    public RoomDto createRoom(RoomDto dto) {
        if (dto.getFloorId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Floor ID is required");
        }
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room name is required");
        }
        Floor floor = floorRepository.findById(dto.getFloorId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));

        Room room = roomMapper.toEntity(dto);
        room.setFloor(floor);
        if (room.getBookingMode() == null) {
            room.setBookingMode(BookingMode.ROOM);
        }
        if (room.getType() == null) {
            room.setType(RoomType.OPEN_SPACE);
        }
        if (room.getSvgElementId() == null || room.getSvgElementId().isBlank()) {
            room.setSvgElementId("room_" + System.currentTimeMillis());
        }

        Room saved = roomRepository.save(room);
        return roomMapper.toDto(saved);
    }

    public RoomDto updateRoom(UUID roomId, RoomDto dto) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        if (dto.getName() != null) room.setName(dto.getName());
        if (dto.getType() != null) room.setType(dto.getType());
        if (dto.getCapacity() != null) room.setCapacity(dto.getCapacity());
        if (dto.getSvgElementId() != null) room.setSvgElementId(dto.getSvgElementId());
        if (dto.getFloorId() != null) {
            Floor floor = floorRepository.findById(dto.getFloorId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Floor not found"));
            room.setFloor(floor);
        }

        return roomMapper.toDto(roomRepository.save(room));
    }

    public void deleteRoom(UUID roomId) {
        Room room = roomRepository.findById(roomId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
        roomRepository.delete(room);
    }

    // ==================== WORKPLACES ====================

    @Transactional(readOnly = true)
    public List<WorkplaceDto> getAllWorkplaces() {
        return workplaceRepository.findAll().stream()
                .map(workplaceMapper::toDto).toList();
    }

    public WorkplaceDto createWorkplace(WorkplaceDto dto) {
        if (dto.getRoomId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Room ID is required");
        }
        if (dto.getCode() == null || dto.getCode().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Workplace code is required");
        }
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));

        Workplace wp = new Workplace();
        wp.setRoom(room);
        wp.setCode(dto.getCode());
        wp.setSvgElementId(dto.getSvgElementId() != null ? dto.getSvgElementId() : "wp_" + System.currentTimeMillis());
        wp.setStatus(dto.getStatus() != null ? dto.getStatus() : WorkplaceStatus.AVAILABLE);
        wp.setEquipment(resolveEquipment(dto.getEquipment()));

        Workplace saved = workplaceRepository.save(wp);
        return workplaceMapper.toDto(saved);
    }

    public WorkplaceDto updateWorkplace(UUID workplaceId, WorkplaceDto dto) {
        Workplace wp = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));

        if (dto.getCode() != null) wp.setCode(dto.getCode());
        if (dto.getSvgElementId() != null) wp.setSvgElementId(dto.getSvgElementId());
        if (dto.getStatus() != null) wp.setStatus(dto.getStatus());
        if (dto.getEquipment() != null) wp.setEquipment(resolveEquipment(dto.getEquipment()));
        if (dto.getRoomId() != null) {
            Room room = roomRepository.findById(dto.getRoomId())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Room not found"));
            wp.setRoom(room);
        }

        return workplaceMapper.toDto(workplaceRepository.save(wp));
    }

    public void deleteWorkplace(UUID workplaceId) {
        Workplace wp = workplaceRepository.findById(workplaceId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Workplace not found"));
        workplaceRepository.delete(wp);
    }

    // ==================== EQUIPMENT ====================

    @Transactional(readOnly = true)
    public List<EquipmentDto> getAllEquipment() {
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::toDto).toList();
    }

    public EquipmentDto createEquipment(EquipmentDto dto) {
        if (dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Equipment name is required");
        }
        equipmentRepository.findByName(dto.getName().trim()).ifPresent(e -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipment with this name already exists");
        });

        Equipment equipment = new Equipment();
        equipment.setName(dto.getName().trim());
        return equipmentMapper.toDto(equipmentRepository.save(equipment));
    }

    public EquipmentDto updateEquipment(UUID equipmentId, EquipmentDto dto) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));

        if (dto.getName() != null && !dto.getName().isBlank()) {
            String newName = dto.getName().trim();
            equipmentRepository.findByName(newName).ifPresent(existing -> {
                if (!existing.getId().equals(equipmentId)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "Equipment with this name already exists");
                }
            });
            equipment.setName(newName);
        }

        return equipmentMapper.toDto(equipmentRepository.save(equipment));
    }

    public void deleteEquipment(UUID equipmentId) {
        Equipment equipment = equipmentRepository.findById(equipmentId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Equipment not found"));

        // Equipment связан с Workplace через @ManyToMany (workplace_equipment).
        // Перед удалением отвязываем его от всех рабочих мест,
        // иначе упадём на FK constraint в join-таблице.
        List<Workplace> linkedWorkplaces = workplaceRepository.findAll().stream()
                .filter(wp -> wp.getEquipment() != null && wp.getEquipment().contains(equipment))
                .toList();
        for (Workplace wp : linkedWorkplaces) {
            wp.getEquipment().remove(equipment);
            workplaceRepository.save(wp);
        }

        equipmentRepository.delete(equipment);
    }

    // ==================== HELPERS ====================

    private BookingDto enrichWithTargetName(BookingDto dto) {
        if (dto.getTargetId() == null || dto.getTargetType() == null) return dto;
        switch (dto.getTargetType()) {
            case ROOM -> roomRepository.findById(dto.getTargetId())
                    .ifPresent(room -> dto.setTargetName(room.getName()));
            case WORKPLACE -> workplaceRepository.findById(dto.getTargetId())
                    .ifPresent(wp -> dto.setTargetName(wp.getCode()));
        }
        return dto;
    }

    /**
     * Превращает набор EquipmentDto (присланных с фронта, обычно только с id)
     * в реальные управляемые JPA-сущности Equipment из БД.
     * Если equipmentDtos == null или пуст - возвращает пустой Set
     * (т.е. снимает всё оборудование с рабочего места).
     */
    private java.util.Set<Equipment> resolveEquipment(java.util.Set<EquipmentDto> equipmentDtos) {
        if (equipmentDtos == null || equipmentDtos.isEmpty()) {
            return new java.util.HashSet<>();
        }
        java.util.Set<UUID> ids = equipmentDtos.stream()
                .map(EquipmentDto::getId)
                .filter(java.util.Objects::nonNull)
                .collect(java.util.stream.Collectors.toSet());

        List<Equipment> found = equipmentRepository.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Одно или несколько устройств не найдены");
        }
        return new java.util.HashSet<>(found);
    }
}
