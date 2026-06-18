package com.plsrflttr.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
        name = "workplaces",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_workplace_svg",
                        columnNames = "svg_element_id"
                )
        }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Workplace {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @Column(nullable = false, unique = true)
    private String code;

    /**
     * id объекта в SVG
     *
     * <rect id="desk_15"/>
     */
    @Column(name = "svg_element_id", nullable = false)
    private String svgElementId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkplaceStatus status;

    @ManyToMany
    @JoinTable(
            name = "workplace_equipment",
            joinColumns = @JoinColumn(name = "workplace_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private Set<Equipment> equipment = new HashSet<>();
}
