package com.restAPI.restAPI;

import com.restAPI.restAPI.user.domain.AppUser;
import com.restAPI.restAPI.user.domain.Role;
import com.restAPI.restAPI.user.service.AppUserService;
import com.restAPI.restAPI.storage.StorageProperties;
import com.restAPI.restAPI.storage.StorageService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@SpringBootApplication
@EnableSwagger2
@EnableConfigurationProperties(StorageProperties.class)

public class RestApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(RestApiApplication.class, args);
	}

	@Bean
	CommandLineRunner run(AppUserService userService){
		return args ->{
			userService.saveRole(new Role(null, "ROLE_USER"));
			userService.saveRole(new Role(null, "ROLE_MANAGER"));
			userService.saveRole(new Role(null, "ROLE_ADMIN"));
			userService.saveRole(new Role(null, "ROLE_SUPER_ADMIN"));

			userService.saveUser((new AppUser(null, "hyunuk", "rohsik2", "rohsik123", new ArrayList<>())));

			userService.addRoleToUser("rohsik2", "ROLE_ADMIN");
			userService.addRoleToUser("rohsik2", "ROLE_MANAGER");
		};
	}

	@Bean
	BCryptPasswordEncoder passwordEncoder(){
		return new BCryptPasswordEncoder();
	}

	@Bean
	CommandLineRunner init(StorageService storageService) {
		return (args) -> {
			storageService.deleteAll();
			storageService.init();
		};
	}
}
