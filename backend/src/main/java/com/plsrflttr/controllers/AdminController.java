//package com.plsrflttr.controllers;
//
//import com.plsrflttr.dto.BookingDto;
//import com.plsrflttr.dto.UserDto;
//import com.plsrflttr.services.AdminService;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.access.prepost.PreAuthorize;
//import org.springframework.web.bind.annotation.*;
//
//import java.util.List;
//import java.util.UUID;
//
//@RestController
//@RequestMapping("/api/admin")
//@PreAuthorize("hasRole('ADMIN')")
//@RequiredArgsConstructor
//public class AdminController {
//    private final AdminService adminService;
//
//    @GetMapping("/users")
//    public ResponseEntity<List<UserDto>> getUsers() {
//        return ResponseEntity.ok(adminService.getAllUsers());
//    }
//
//    @PatchMapping("/users/{userId}/block")
//    public ResponseEntity<Void> blockUser(
//            @PathVariable UUID userId
//    ) {
//        adminService.blockUser(userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PatchMapping("/users/{userId}/unblock")
//    public ResponseEntity<Void> unblockUser(
//            @PathVariable UUID userId
//    ) {
//        adminService.unblockUser(userId);
//        return ResponseEntity.noContent().build();
//    }
//
//    @GetMapping("/bookings")
//    public ResponseEntity<List<BookingDto>> getBookings() {
//        return ResponseEntity.ok(adminService.getAllBookings());
//    }
//
//    @PatchMapping("/bookings/{bookingId}/cancel")
//    public ResponseEntity<Void> cancelBooking(
//            @PathVariable UUID bookingId
//    ) {
//        adminService.cancelBooking(bookingId);
//        return ResponseEntity.noContent().build();
//    }
//
//}
