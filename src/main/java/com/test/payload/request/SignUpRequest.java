package com.test.payload.request;

import java.util.Set;

import com.test.model.Role;

public class SignUpRequest {
	
	private String username;
	private String password;
	private String email;
	private Set<String>roles;
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public Set<String> getRoles() {
		return roles;
	}
	public void setRoles(Set<String> roles) {
		this.roles = roles;
	}
	
	@Override
	public String toString() {
		return "SignUpRequest [username=" + username + ", email=" + email + ", roles=" + roles + "]";
	}
	
}