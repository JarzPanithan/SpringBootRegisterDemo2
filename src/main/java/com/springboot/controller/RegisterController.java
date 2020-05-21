package com.springboot.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import com.springboot.model.UserDto;
import com.springboot.repository.ConfirmationTokenRepository;
import com.springboot.service.UserService;

@Controller
public class RegisterController {
	@Autowired
	private UserService userService;
	
	@Autowired
	private ConfirmationTokenRepository confirmationTokenRepository;
	
	@GetMapping("/register")
	public ModelAndView viewRegisterPage() {
		ModelAndView model = new ModelAndView();
		UserDto userDto = new UserDto();
		model.addObject("user", userDto);
		model.setViewName("register");
		return model;
	}
	
	@PostMapping("/register")
	public ModelAndView registerUser(@Valid @ModelAttribute("user") UserDto userDto, BindingResult result) {
		ModelAndView model = new ModelAndView();
		User existing = userService.findByEmail(userDto.getEmail());
		if (existing != null) {
			model.addObject("message", "This email is already use!!");
			model.setViewName("error");
			return model;
		}
		if (result.hasErrors()) {
			model.setViewName("register");
			return model;
		}
		userService.saveUser(userDto);
		model.addObject("email", userDto.getEmail());
		model.setViewName("registrationSuccess");
		return model;
	}
	
	@RequestMapping(value = "/confirm-account", method = {RequestMethod.GET, RequestMethod.POST})
	public ModelAndView confirmUser(@RequestParam("token") String confirmationToken) {
		ModelAndView model = new ModelAndView();
		ConfirmationToken token = confirmationTokenRepository.findByConfirmationToken(confirmationToken);
		if (token != null) {
			User user = userService.findByEmail(token.getUser().getEmail());
			user.setEnabled(true);
			userService.enabledRegisterUser(user);
			model.setViewName("success");
			return model;
		} else {
			model.addObject("message", "This link is invalid or broken!!");
			model.setViewName("error");
		}
		return model;
	}
}
