package com.plsrflttr.configs;

import com.plsrflttr.models.Role;
import com.plsrflttr.models.User;
import com.plsrflttr.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AdminProperties adminProperties;

    @Override
    public void run(String... args) {

        if (adminProperties.getEmail() == null ||
                adminProperties.getPassword() == null) {
            return;
        }

        boolean exists = userRepository.existsByEmail(
                adminProperties.getEmail()
        );

        if (exists) {
            return;
        }

        User admin = new User();

        admin.setEmail(adminProperties.getEmail());

        admin.setPasswordHash(
                passwordEncoder.encode(
                        adminProperties.getPassword()
                )
        );

        admin.setFirstName(
                adminProperties.getFirstName()
        );

        admin.setLastName(
                adminProperties.getLastName()
        );

        admin.getRoles().add(Role.USER);
        admin.getRoles().add(Role.ADMIN);

        admin.setEnabled(true);

        userRepository.save(admin);

        System.out.println(
                "ADMIN USER CREATED: "
                        + adminProperties.getEmail()
        );
    }
}