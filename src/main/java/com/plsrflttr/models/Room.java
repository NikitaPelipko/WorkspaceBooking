package com.plsrflttr.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Room {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "floor_id", nullable = false)
    private Floor floor;

    @Column(nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RoomType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingMode bookingMode;

    private Integer capacity;

    /**
     * id элемента внутри SVG
     * Например: room_101
     */
    @Column(unique = true, nullable = false)
    private String svgElementId;

    @OneToMany(mappedBy = "room")
    private List<Workplace> workplaces = new ArrayList<>();
}