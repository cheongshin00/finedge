package com.finedge.finedgeapi.config;

import com.finedge.finedgeapi.entity.Role;
import com.finedge.finedgeapi.entity.User;
import com.finedge.finedgeapi.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;

@Configuration
public class DataInit {

    @Bean
    CommandLineRunner initUsers(UserRepository userRepository, PasswordEncoder encoder) {
        return args -> {
            if (userRepository.findByUsername("admin").isEmpty()){
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(encoder.encode("admin"));
                HashSet<Role> role1 = new HashSet<Role>();
                role1.add(Role.ADMIN);
                role1.add(Role.CUSTOMER);
                admin.setRole(role1);
                userRepository.save(admin);
                System.out.println("Admin user created: username=admin, password=admin");
            }
        };

    }
}
