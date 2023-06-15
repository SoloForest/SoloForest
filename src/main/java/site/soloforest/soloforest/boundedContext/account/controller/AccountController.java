package site.soloforest.soloforest.boundedContext.account.controller;

import java.io.IOException;
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
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.base.security.AccountAdapter;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.dto.FindPasswordForm;
import site.soloforest.soloforest.boundedContext.account.dto.FindUsernameForm;
import site.soloforest.soloforest.boundedContext.account.dto.ModifyForm;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/account")
public class AccountController {
	private final AccountService accountService;
	private final Rq rq;

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
	public String modifyMe(@PathVariable Long id, @Valid @ModelAttribute ModifyForm input,
		Model model, HttpServletRequest request) throws IOException {
		RsData<Account> rsAccount = accountService.modifyInfo(id, input, request);
		if (rsAccount.isFail()) {
			return rq.historyBack(rsAccount);
		}
		model.addAttribute("account", rsAccount.getData());
		return rq.redirectWithMsg("/account/me", rsAccount);
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

	@PostMapping("/report")
	@PreAuthorize("isAuthenticated()")
	public ResponseEntity<String> report(@RequestParam Long id,
		@AuthenticationPrincipal AccountAdapter accountAdapter) {
		if (accountAdapter.getId().equals(id)) {
			return new ResponseEntity<>("자기 자신을 신고할 수 없습니다.", HttpStatus.BAD_REQUEST);
		}
		return accountService.report(id);
	}

	@GetMapping("/find")
	@PreAuthorize("isAnonymous()")
	public String find() {
		return "account/find_account";
	}

	@PostMapping("/find/username")
	@ResponseBody
	public String findUsername(@Valid @ModelAttribute FindUsernameForm form) {
		return accountService.findUsername(form.getEmail());
	}

	@PostMapping("/find/password")
	@ResponseBody
	public String findPassword(@Valid @ModelAttribute FindPasswordForm form) {
		return accountService.findPassword(form.getEmail(), form.getUsername());
	}
}