package com.sv.youapp.bff.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/oauth2")
public class OAuth2RedirectController {

	/**
	 * Redirects the user to the specified application with the provided session ID (sid).
	 * This method acts as a fallback if App Links fails
	 *
	 * @param sid the session ID to include in the redirect URL
	 */
	@GetMapping("/redirect")
	public String redirect(
		@RequestParam("sid") String sid,
		Model model
	) {
		model.addAttribute("sid", sid);
		return "redirect";
	}
}
