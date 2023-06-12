package site.soloforest.soloforest.boundedContext.account.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.security.AccountAdapter;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.dto.ModifyForm;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	private final AccountService accountService;

	@GetMapping("/login")
	@PreAuthorize("isAnonymous()")
	public String showLogin() {
		return "account/login";
	}

	@GetMapping("/terms")
	@PreAuthorize("isAnonymous()")
	public String showTerms() {
		return "account/terms";
	}

	@GetMapping("/signUp")
	@PreAuthorize("isAnonymous()")
	public String showSignUp() {
		return "account/sign_up";
	}

	@PostMapping("/signUp")
	@PreAuthorize("isAnonymous()")
	public String signup(@Valid @ModelAttribute AccountDTO input, HttpServletRequest request) {
		accountService.singup(input);
		accountService.authenticateAccountAndSetSession(input, request);
		return "redirect:/main";
	}

	@GetMapping("/me")
	@PreAuthorize("isAuthenticated()")
	public String showMe(Principal principal, Model model) {
		Account entity = accountService.getAccountFromUsername(principal.getName());
		model.addAttribute("account", entity);
		return "account/me";
	}

	@PostMapping("/me/{id}")
	@PreAuthorize("isAuthenticated()")
	public String modifyMe(@PathVariable Long id, @ModelAttribute ModifyForm input,
		Model model, HttpServletRequest request) {
		Account entity = accountService.modifyInfo(id, input, request);
		if (entity == null)
			return "redirect:/account/me?error=true";
		model.addAttribute("account", entity);
		return "redirect:/account/me";
	}

	@PostMapping("/withdraw/{id}")
	@PreAuthorize("isAuthenticated()")
	@ResponseBody
	public ResponseEntity<String> withdraw(@PathVariable Long id, @RequestParam("password") String password,
		@AuthenticationPrincipal AccountAdapter accountAdapter, HttpServletRequest request) {
		if (!accountAdapter.getId().equals(id)) {
			return new ResponseEntity<>("Authentication failed", HttpStatus.UNAUTHORIZED);
		}

		return accountService.withdraw(accountAdapter.getId(), password, request);
	}

	@PostMapping("/report/{id}")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> report(@PathVariable Long id,
		@AuthenticationPrincipal AccountAdapter accountAdapter) {
		if (accountAdapter.getId().equals(id)) {
			return new ResponseEntity<>("자기 자신을 신고할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		return accountService.report(id);
	}

	@GetMapping("/findAccount")
	@PreAuthorize("isAnonymous()")
	public String find() {
		return "account/find_account";
	}
}
