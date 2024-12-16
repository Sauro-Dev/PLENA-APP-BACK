package com.plenamente.sgt;

import com.plenamente.sgt.domain.entity.Admin;
import com.plenamente.sgt.domain.entity.Plan;
import com.plenamente.sgt.domain.entity.Rol;
import com.plenamente.sgt.infra.repository.PlanRepository;
import com.plenamente.sgt.infra.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.context.annotation.Bean;
import java.time.LocalDate;

@SpringBootApplication
@RequiredArgsConstructor
public class SgtApplication {

	private final UserRepository userRepository;
	private final PasswordEncoder passwordEncoder;
	private final PlanRepository planRepository;

	public static void main(String[] args) {
		SpringApplication.run(SgtApplication.class, args);
	}

	@Bean
	public CommandLineRunner initDatabase() {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				// Crear un Admin por defecto
				Admin defaultAdmin = new Admin();
				defaultAdmin.setName("Lozano");
				defaultAdmin.setPaternalSurname("Admin");
				defaultAdmin.setMaternalSurname("");
				defaultAdmin.setDni("00000000");
				defaultAdmin.setPhone("000000000");
				defaultAdmin.setPhoneBackup("000000000");
				defaultAdmin.setAddress("Admin Address");
				defaultAdmin.setEmail("admin@example.com");
				defaultAdmin.setBirthdate(LocalDate.parse("2021-01-01"));
				defaultAdmin.setUsername("admin");
				defaultAdmin.setPassword(passwordEncoder.encode("admin123"));
				defaultAdmin.setRol(Rol.ADMIN);
				defaultAdmin.setEnabled(true);

				userRepository.save(defaultAdmin);
			}
			if(planRepository.findAll().isEmpty()) {
				Plan planA = new Plan();
				Plan planB = new Plan();
				Plan planC = new Plan();
				Plan planD = new Plan();

				planA.setNumOfSessions(1);
				planB.setNumOfSessions(2);
				planC.setNumOfSessions(3);
				planD.setNumOfSessions(4);
				planRepository.save(planA);
				planRepository.save(planB);
				planRepository.save(planC);
				planRepository.save(planD);
			}
		};
	}
}
