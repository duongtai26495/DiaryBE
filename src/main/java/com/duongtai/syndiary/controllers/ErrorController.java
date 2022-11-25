package com.duongtai.syndiary.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ErrorController {

	@GetMapping("/access-denied")
	public String getAccessDenied() {
		return "/error/access-denied";
	}
}
