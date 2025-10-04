package com.trouni.tro_uni;

import com.trouni.tro_uni.entity.User;
import com.trouni.tro_uni.entity.Profile;
import com.trouni.tro_uni.enums.UserRole;
import com.trouni.tro_uni.enums.AccountStatus;
import com.trouni.tro_uni.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

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
		createDefaultAdmin();
		
		// Tạo tài khoản student mặc định
		createDefaultStudent();
		
		// Tạo tài khoản landlord mặc định
		createDefaultLandlord();
	}
	
	private void createDefaultAdmin() {
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
	
	private void createDefaultStudent() {
		String studentEmail = "student@trouni.com";
		if (!userRepository.existsByRole(UserRole.STUDENT)) {
			User student = new User();
			student.setUsername("student");
			student.setEmail(studentEmail);
			student.setPassword(passwordEncoder.encode("12345678"));
			student.setRole(UserRole.STUDENT);
			student.setStatus(AccountStatus.ACTIVE);
			student.setPhoneVerified(true);
			student.setGoogleAccount(false);
			student.setCreatedAt(LocalDateTime.now());
			student.setUpdatedAt(LocalDateTime.now());
			
			// Tạo profile cho student
			Profile studentProfile = new Profile();
			studentProfile.setFullName("Default Student");
			studentProfile.setPhoneNumber("0123456789");
			studentProfile.setGender("Male");
			studentProfile.setBadge("Student");
			student.setProfile(studentProfile);
			
			userRepository.save(student);
			System.out.println("=== STUDENT ACCOUNT CREATED ===");
			System.out.println("Username: student");
			System.out.println("Email: " + studentEmail);
			System.out.println("Password: student123");
			System.out.println("=================================");
		}
	}
	
	private void createDefaultLandlord() {
		String landlordEmail = "landlord@trouni.com";
		if (!userRepository.existsByRole(UserRole.LANDLORD)) {
			User landlord = new User();
			landlord.setUsername("landlord");
			landlord.setEmail(landlordEmail);
			landlord.setPassword(passwordEncoder.encode("12345678"));
			landlord.setRole(UserRole.LANDLORD);
			landlord.setStatus(AccountStatus.ACTIVE);
			landlord.setPhoneVerified(true);
			landlord.setGoogleAccount(false);
			landlord.setCreatedAt(LocalDateTime.now());
			landlord.setUpdatedAt(LocalDateTime.now());
			
			// Tạo profile cho landlord
			Profile landlordProfile = new Profile();
			landlordProfile.setFullName("Default Landlord");
			landlordProfile.setPhoneNumber("0987654321");
			landlordProfile.setGender("Female");
			landlordProfile.setBadge("Landlord");
			landlord.setProfile(landlordProfile);
			
			userRepository.save(landlord);
			System.out.println("=== LANDLORD ACCOUNT CREATED ===");
			System.out.println("Username: landlord");
			System.out.println("Email: " + landlordEmail);
			System.out.println("Password: 12345678");
			System.out.println("==================================");
		}
	}
}
