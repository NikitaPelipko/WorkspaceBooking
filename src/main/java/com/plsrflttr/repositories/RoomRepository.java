package com.plsrflttr.repositories;

import com.plsrflttr.models.Room;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByFloorId(UUID floorId);

    Optional<Room> findBySvgElementId(String svgElementId);
}

