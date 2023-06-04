package site.soloforest.soloforest.boundedContext.account.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	private final AccountService accountService;

	@GetMapping("/login")
	@PreAuthorize("isAnonymous()")
	public String showLogin() {
		return "/account/login";
	}

	@GetMapping("/signUp")
	@PreAuthorize("isAnonymous()")
	public String showSignUp() {
		return "/account/sign_up";
	}

	@PostMapping("/signUp")
	@PreAuthorize("isAnonymous()")
	public String signup(@ModelAttribute AccountDTO input) {
		accountService.singup(input);
		return "redirect:/main";
	}
}
