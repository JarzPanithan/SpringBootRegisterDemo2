package com.springboot.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class MainController {
	@GetMapping("/login")
	public String viewLoginPage() {
		return "login";
	}
	
	@GetMapping("/success")
	public String viewSuccessRegisterPage() {
		return "success";
	}
	
	@GetMapping("/error")
	public String viewErrorRegisterPage() {
		return "error";
	}
	
	@GetMapping
	public String viewHomePage() {
		return "home";
	}
}
