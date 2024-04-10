package com.test.controller;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.test.model.ERole;
import com.test.model.Role;
import com.test.model.User;
import com.test.payload.request.LoginRequest;
import com.test.payload.request.SignUpRequest;
import com.test.payload.response.MessageResponse;
import com.test.payload.response.UserInfoResponse;
import com.test.repository.RoleRepository;
import com.test.repository.UserRepository;
import com.test.security.jwt.JWTUtils;
import com.test.security.service.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	Logger logger = Logger.getLogger(TestController.class);
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private RoleRepository roleRepository;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserDetailsService userDetailsService;
	@Autowired
	private JWTUtils jwtutils;

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@RequestBody SignUpRequest signUpRequest, HttpServletRequest request) {
		// Check username if exists throw exception
		String endpoint = ServletUriComponentsBuilder.fromRequestUri(request).build().toUriString();
		logger.info("API Endpoint:" + endpoint);
		logger.info("SignUpRequest:" + signUpRequest);
		if (userRepository.existsByUsername(signUpRequest.getUsername())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Username is already taken!"));
		}
		// Check email if exists throw exception
		if (userRepository.existsByEmail(signUpRequest.getPassword())) {
			return ResponseEntity.badRequest().body(new MessageResponse("Error: Email is already in use!"));
		}
		System.out.println("Password:" + signUpRequest.getPassword());
		// Encode the password
		String encodePassword = passwordEncoder.encode(signUpRequest.getPassword());
		// Create a User
		User user = new User(signUpRequest.getUsername(), signUpRequest.getEmail(), encodePassword);
		Set<String> inputRoles = signUpRequest.getRoles();
		HashSet<Role> roles = new HashSet<>();
		if (inputRoles == null) {
			Role userRole = roleRepository.findByName(ERole.ROLE_USER)
					.orElseThrow(() -> new RuntimeException("No role found"));
			roles.add(userRole);

		} else {
			inputRoles.forEach((role) -> {
				switch (role) {
				case "admin":
					Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
							.orElseThrow(() -> new RuntimeException("No role found"));
					roles.add(adminRole);
				case "mod":
					Role modRole = roleRepository.findByName(ERole.ROLE_MODERATOR)
							.orElseThrow(() -> new RuntimeException("No role found"));
					roles.add(modRole);
				case "user":
					Role userRole = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("No role found"));
					roles.add(userRole);
				default:
					Role userRoledefault = roleRepository.findByName(ERole.ROLE_USER)
							.orElseThrow(() -> new RuntimeException("No role found"));
					if (!roles.contains(userRoledefault))
						roles.add(userRoledefault);

				}
			});
		}
		user.setRoles(roles);
		userRepository.save(user);
		return ResponseEntity.ok(new MessageResponse("User registered successfully!"));

	}

	@PostMapping("/login")
	public ResponseEntity<UserInfoResponse> login(@RequestBody LoginRequest loginRequest, HttpServletRequest request) {
		String endpoint = ServletUriComponentsBuilder.fromRequestUri(request).build().toUriString();
		logger.info("API Endpoint:" + endpoint);
		logger.info("Login Request" + loginRequest);
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		// authentication.getPrincipal(); is type of UserDetails
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		logger.info("UserDetails:" + userDetails);
		ResponseCookie jwtCookie = jwtutils.generateJwtCookie(userDetails);
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority())
				.collect(Collectors.toList());

		return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, jwtCookie.toString()).body(
				new UserInfoResponse(userDetails.getId(), userDetails.getUsername(), userDetails.getEmail(), roles));

	}

}
