package com.test.security.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.test.model.Role;
import com.test.model.User;

//This class contains details about User
//This class will get a data from UserDetailsImpl
//UserDetailsImpl loads the data from DB based on user input on Login form
public class UserDetailsImpl implements UserDetails {

	private static final long serialVersionUID = 1L;
	private Long id;
	@JsonIgnore
	private String password;
	private String username;
	private String email;
	private Collection<? extends GrantedAuthority> authorities;

	public UserDetailsImpl(Long id, String password, String username, String email,
			Collection<? extends GrantedAuthority> authorities) {
		super();
		this.id = id;
		this.password = password;
		this.username = username;
		this.email = email;
		this.authorities = authorities;
	}

	
	public Long getId() {
		return id;
	}


	public void setId(Long id) {
		this.id = id;
	}


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
		this.authorities = authorities;
	}


	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {

		return this.authorities;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	@Override
	public String getUsername() {
		return this.username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	// Building UserDetails Object
	public static UserDetailsImpl build(User user) {
		Set<Role> fromDb = user.getRoles();
		GrantedAuthority grantedAuth;
		List<GrantedAuthority> finalAuthorities = new ArrayList<>();
		for (Role role : fromDb) {
			grantedAuth = new SimpleGrantedAuthority(role.getName().name());
			finalAuthorities.add(grantedAuth);
		}

		return new UserDetailsImpl(user.getId(), user.getPassword(), user.getUsername(), user.getEmail(),
				finalAuthorities);
	}


	@Override
	public String toString() {
		return "[id=" + id + ", password=" + password + ", username=" + username + ", email=" + email
				+ ", authorities=" + authorities + "]";
	}

	
}
