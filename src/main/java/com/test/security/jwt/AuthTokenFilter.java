package com.test.security.jwt;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.test.security.service.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AuthTokenFilter extends OncePerRequestFilter {
	
	@Autowired
	  private JWTUtils jwtUtils;

	  @Autowired
	  private UserDetailsServiceImpl userDetailsService;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		Logger logger = Logger.getLogger(AuthTokenFilter.class);
		 try {
		      String jwt = parseJwt(request);
		      logger.info("Request From:"+request.getServletPath());
		      logger.info("JWT Cookie from Cookies:"+jwt+"Expires at:"+jwtUtils.getTokenExpiration(jwt));
		      if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
		        String username = jwtUtils.getUserNameFromJwtToken(jwt);

		        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
		        
		        UsernamePasswordAuthenticationToken authentication = 
		            new UsernamePasswordAuthenticationToken(userDetails,
		                                                    null,
		                                                    userDetails.getAuthorities());
		        
		        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		        SecurityContextHolder.getContext().setAuthentication(authentication);
		      }
		    } catch (Exception e) {
		      logger.error(e.getMessage());
		    }

		    filterChain.doFilter(request, response);
		  }

		  private String parseJwt(HttpServletRequest request) {
		    String jwt = jwtUtils.getJwtFromCookies(request);
		    return jwt;
		  }
	
}
