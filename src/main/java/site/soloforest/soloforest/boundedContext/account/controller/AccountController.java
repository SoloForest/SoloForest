package site.soloforest.soloforest.boundedContext.account.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	@Autowired
	private final AccountRepository accountRepository;
	@Autowired
	private final PasswordEncoder passwordEncoder;

	@GetMapping("/login")
	@PreAuthorize("isAnonymous()")
	public String showLogin() {
		return "/account/login";
	}

	@GetMapping("/signUp")
	@PreAuthorize("isAnonymous()")
	public String signup() {

		Account account = Account.builder()
			.username("usertest")
			.password(passwordEncoder.encode("test1"))
			.nickname("for test")
			.email("test@test.com")
			.build();
		this.accountRepository.save(account);
		return "redirect:/account/login";
	}
}
