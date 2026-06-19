package com.plsrflttr.repositories;

import com.plsrflttr.models.Room;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {
    List<Room> findByFloorId(UUID floorId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
    select r
    from Room r
    where r.id = :id
""")
    Optional<Room> findByIdForUpdate(UUID id);
}

