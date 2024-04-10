package com.test.controller;

import org.apache.log4j.Logger;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/test")
public class TestController {
	Logger logger = Logger.getLogger(TestController.class);

	@GetMapping("/all")
	public String allAccess(HttpServletRequest request) {
		String endpoint = ServletUriComponentsBuilder.fromRequestUri(request).build().toUriString();
		logger.info("API EndPoint:" + endpoint);
		return "Public Content.";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('MODERATOR') or hasRole('ADMIN')")
	public String userAccess(HttpServletRequest request) {
		String endpoint = ServletUriComponentsBuilder.fromRequestUri(request).build().toUriString();
		logger.info("API EndPoint:" + endpoint);
		return "User Content.";
	}

	@GetMapping("/mod")
	@PreAuthorize("hasRole('MODERATOR') or hasRole('ADMIN')")
	public String modAccess(HttpServletRequest request) {
		String endpoint = ServletUriComponentsBuilder.fromRequestUri(request).build().toUriString();
		logger.info("API EndPoint:" + endpoint);
		return "Mod Content.";
	}

}
