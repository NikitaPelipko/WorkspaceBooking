package com.plsrflttr.repositories;

import com.plsrflttr.models.Workplace;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkplaceRepository extends JpaRepository<Workplace, UUID> {
    Optional<Workplace> findByCode(String code);

    Optional<Workplace> findBySvgElementId(String svgElementId);

    List<Workplace> findByRoomId(UUID roomId);
}

