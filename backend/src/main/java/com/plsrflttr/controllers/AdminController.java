package com.plsrflttr.controllers;

import com.plsrflttr.dto.BookingDto;
import com.plsrflttr.dto.UserDto;
import com.plsrflttr.models.BookingStatus;
import com.plsrflttr.services.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {
    private final AdminService adminService;

    // ==================== USERS ====================

    @GetMapping("/users")
    public ResponseEntity<List<UserDto>> getUsers() {
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/block")
    public ResponseEntity<Void> blockUser(
            @PathVariable UUID userId
    ) {
        adminService.blockUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/unblock")
    public ResponseEntity<Void> unblockUser(
            @PathVariable UUID userId
    ) {
        adminService.unblockUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ==================== BOOKINGS ====================

    @GetMapping("/bookings")
    public ResponseEntity<List<BookingDto>> getBookings() {
        return ResponseEntity.ok(adminService.getAllBookings());
    }

    @PatchMapping("/bookings/{bookingId}/status")
    public ResponseEntity<BookingDto> updateBookingStatus(
            @PathVariable UUID bookingId,
            @RequestParam BookingStatus status
    ) {
        return ResponseEntity.ok(adminService.updateBookingStatus(bookingId, status));
    }
}
