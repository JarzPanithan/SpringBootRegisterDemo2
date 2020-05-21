package com.springboot.service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.springboot.model.ConfirmationToken;
import com.springboot.model.Role;
import com.springboot.model.User;
import com.springboot.model.UserDto;
import com.springboot.repository.ConfirmationTokenRepository;
import com.springboot.repository.UserRepository;

@Service
public class UserServiceImpl implements UserService {
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	
	@Autowired
	private EmailMailSender emailMailSender;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@Override
	@Transactional
	public User findByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	@Override
	@Transactional
	public void saveUser(UserDto userDto) {
		User user = new User();
		user.setFirstName(userDto.getFirstName());
		user.setLastName(userDto.getLastName());
		user.setEmail(userDto.getEmail());
		user.setPassword(bCryptPasswordEncoder.encode(userDto.getPassword()));
		user.setRoles(Arrays.asList(new Role("ROLE_USER")));
		user.setEnabled(userDto.isEnabled());
		userRepository.save(user);
		ConfirmationToken confirmationToken = new ConfirmationToken(user);
		confirmationTokenRepository.save(confirmationToken);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("official@gmail.com");
		mailMessage.setSubject("Verify Email");
		mailMessage.setText("To confirm your account, please click here: " + "http://localhost:8080/confirm-account?token=" +
							confirmationToken.getConfirmationToken());
		emailMailSender.sendEmail(mailMessage);
	}
	
	@Override
	@Transactional
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(email);
		if (user == null) {
			throw new UsernameNotFoundException("Invalid email or password.");
		}
		Set<GrantedAuthority> grantedAuthorities = new HashSet<>();
		for (Role role : user.getRoles()) {
			grantedAuthorities.add(new SimpleGrantedAuthority(role.getName()));
		}
		return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), user.isEnabled(),
				true, true, true, grantedAuthorities);
	}
	
	@Override
	@Transactional
	public void enabledRegisterUser(User user) {
		userRepository.save(user);
	}
	
	@Override
	@Transactional
	public void resetPassword(User user) {
		User existingUser = userRepository.findByEmail(user.getEmail());
		ConfirmationToken confirmationToken = new ConfirmationToken(existingUser);
		confirmationTokenRepository.save(confirmationToken);
		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setTo(user.getEmail());
		mailMessage.setFrom("official@gmail.com");
		mailMessage.setSubject("Reset Password");
		mailMessage.setText("To complete the password reset process, please click here: " + "http://localhost:8080/confirm-reset?token=" +
							confirmationToken.getConfirmationToken());
		emailMailSender.sendEmail(mailMessage);
	}
	
	@Override
	@Transactional
	public ConfirmationToken findByConfirmationToken(String confirmationToken) {
		return confirmationTokenRepository.findByConfirmationToken(confirmationToken);
	}
}
