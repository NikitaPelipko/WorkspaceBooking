package com.plsrflttr.repositories;

import com.plsrflttr.models.Equipment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {
    Optional<Equipment> findByName(String name);
}

