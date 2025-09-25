package com.trouni.tro_uni;

import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.AccountStatus;
import com.trouni.tro_uni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootApplication
public class TroUniApplication implements CommandLineRunner {

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;

	public static void main(String[] args) {
		SpringApplication.run(TroUniApplication.class, args);
	}

	@Override
	public void run(String... args) {
		// Tạo tài khoản admin nếu chưa có
		if (!userRepository.existsByRole(UserRole.ADMIN)) {
			User admin = new User();
			admin.setUsername("admin");
			admin.setEmail("admin@gmail.com");
			admin.setPassword(passwordEncoder.encode("12345678"));
			admin.setRole(UserRole.ADMIN);
			admin.setStatus(AccountStatus.ACTIVE);
			admin.setPhoneVerified(true);
			admin.setGoogleAccount(false);
			
			userRepository.save(admin);
			System.out.println("=== ADMIN ACCOUNT CREATED ===");
			System.out.println("Username: admin");
			System.out.println("Email: admin@gmail.com");
			System.out.println("Password: 12345678");
			System.out.println("=============================");
		}
	}
}
