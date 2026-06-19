//package com.plsrflttr.services;
//
//import com.plsrflttr.dto.BookingDto;
//import com.plsrflttr.dto.UserDto;
//import com.plsrflttr.mappers.BookingMapper;
//import com.plsrflttr.mappers.UserMapper;
//import com.plsrflttr.models.Booking;
//import com.plsrflttr.models.BookingStatus;
//import com.plsrflttr.models.User;
//import com.plsrflttr.repositories.BookingRepository;
//import com.plsrflttr.repositories.UserRepository;
//import lombok.RequiredArgsConstructor;
//import org.springframework.http.HttpStatus;
//import org.springframework.stereotype.Service;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.server.ResponseStatusException;
//
//import java.util.List;
//import java.util.UUID;
//
//@Service
//@RequiredArgsConstructor
//@Transactional
//public class AdminService {
//
//    private final UserRepository userRepository;
//    private final BookingRepository bookingRepository;
//    private final UserMapper userMapper;
//    private final BookingMapper bookingMapper;
//
//    public List<UserDto> getAllUsers() {
//
//        return userRepository.findAll()
//                .stream()
//                .map(userMapper::toDto)
//                .toList();
//    }
//
//    public void blockUser(UUID userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "User not found"
//                        ));
//
//        user.setEnabled(false);
//
//        userRepository.save(user);
//    }
//
//    public void unblockUser(UUID userId) {
//
//        User user = userRepository.findById(userId)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "User not found"
//                        ));
//
//        user.setEnabled(true);
//
//        userRepository.save(user);
//    }
//
//    public List<BookingDto> getAllBookings() {
//
//        return bookingRepository.findAll()
//                .stream()
//                .map(bookingMapper::toDto)
//                .toList();
//    }
//
//    public void cancelBooking(UUID bookingId) {
//
//        Booking booking = bookingRepository.findById(bookingId)
//                .orElseThrow(() ->
//                        new ResponseStatusException(
//                                HttpStatus.NOT_FOUND,
//                                "Booking not found"
//                        ));
//
//        booking.setStatus(BookingStatus.CANCELLED);
//
//        bookingRepository.save(booking);
//    }
//}