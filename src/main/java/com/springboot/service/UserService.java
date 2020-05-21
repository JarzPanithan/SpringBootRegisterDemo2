package com.springboot.service;

import org.springframework.security.core.userdetails.UserDetailsService;

import com.springboot.model.ConfirmationToken;
import com.springboot.model.User;
import com.springboot.model.UserDto;

public interface UserService extends UserDetailsService {
	public User findByEmail(String email);
	
	public void saveUser(UserDto userDto);
	
	public void enabledRegisterUser(User user);
	
	public ConfirmationToken findByConfirmationToken(String confirmationToken);
	
	public void resetPassword(User user);
}
