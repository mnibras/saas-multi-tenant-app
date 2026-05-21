package com.nibras.saas;

import com.nibras.saas.entity.User;
import com.nibras.saas.enums.UserRole;
import com.nibras.saas.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public CommandLineRunner commandLineRunner(final UserRepository userRepository, final PasswordEncoder passwordEncoder) {
        return args -> {
            boolean existsByRole = userRepository.existsByRole(UserRole.ROLE_PLATFORM_ADMIN);
            if (!existsByRole) {
                User adminUser = new User();
                adminUser.setUsername("admin");
                adminUser.setPassword(passwordEncoder.encode("password"));
                adminUser.setFirstName("Admin");
                adminUser.setLastName("User");
                adminUser.setEmail("admin@app.com");
                adminUser.setCreatedBy("System");
                adminUser.setEnabled(true);
                adminUser.setRole(UserRole.ROLE_PLATFORM_ADMIN);
                userRepository.save(adminUser);
            }
        };
    }

}
