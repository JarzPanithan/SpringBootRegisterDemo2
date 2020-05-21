package com.springboot.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.springboot.model.ConfirmationToken;
import com.springboot.model.User;
import com.springboot.service.UserService;

@Controller
public class FotgotPasswordController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	@GetMapping("/forgot-password")
	public ModelAndView viewForgotPasswordPage() {
		ModelAndView model = new ModelAndView();
		model.addObject("user", new User());
		model.setViewName("forgotPassword");
		return model;
	}
	
	@PostMapping("/forgot-password")
	public ModelAndView resetPassword(@Valid @ModelAttribute("user") User user, BindingResult result) {
		ModelAndView model = new ModelAndView();
		User existing = userService.findByEmail(user.getEmail());
		if (result.hasErrors()) {
			model.setViewName("forgotPassword");
			return model;
		}
		if (existing != null) {
			userService.resetPassword(user);
			model.addObject("email", user.getEmail());
			model.setViewName("forgotSuccess");
			return model;
		} else {
			model.addObject("message", "This email does not exist!!");
			model.setViewName("error");
		}
		return model;
	}
	
	@RequestMapping(value = "/confirm-reset", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmResetPassword(@RequestParam("token") String confirmationToken) {
		ModelAndView model = new ModelAndView();
		ConfirmationToken token = userService.findByConfirmationToken(confirmationToken);
		if (token != null) {
			User user = userService.findByEmail(token.getUser().getEmail());
			user.setEnabled(true);
			userService.enabledRegisterUser(user);
			model.addObject("user", user);
			model.setViewName("resetPassword");
			return model;
		} else {
			model.addObject("message", "This link is invalid or broken!!");
			model.setViewName("error");
		}
		return model;
	}
	
	@PostMapping("/reset-password")
	public ModelAndView changePassword(User user) {
		ModelAndView model = new ModelAndView();
		if (user.getEmail() != null) {
			User tokenUser = userService.findByEmail(user.getEmail());
			tokenUser.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
			userService.enabledRegisterUser(tokenUser);
			model.addObject("user", user);
			model.setViewName("changePassword");
			return model;
		} else {
			model.addObject("message", "This link is invalid or broken!!");
			model.setViewName("error");
		}
		return model;
	}
}
