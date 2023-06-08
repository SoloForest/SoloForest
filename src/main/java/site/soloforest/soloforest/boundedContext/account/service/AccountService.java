package site.soloforest.soloforest.boundedContext.account.service;

import java.util.Optional;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.dto.ModifyForm;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;

	private final AuthenticationManager authenticationManager;

	public Account singup(AccountDTO dto) {
		Account account = Account.builder()
			.username(dto.getUsername())
			.password(dto.getPassword())
			.nickname(dto.getNickname())
			.email(dto.getEmail())
			.address(dto.getAddress())
			.build();
		return accountRepository.save(account);
	}

	public void authenticateAccountAndSetSession(AccountDTO account, HttpServletRequest request) {
		// 사용자 인증
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			account.getUsername(),
			account.getPassword()
		);
		try {
			// AuthenticationManager 에 token 을 넘기면 UserDetailsService 가 받아 처리하도록 한다.
			Authentication authentication = authenticationManager.authenticate(token);
			// 실제 SecurityContext 에 authentication 정보를 등록한다.
			SecurityContextHolder.getContext().setAuthentication(authentication);
			// 실제 사용자의 세션에 context를 저장한다.
			HttpSession session = request.getSession();
			session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());
		} catch (DisabledException | LockedException | BadCredentialsException e) {
			String status;
			if (e.getClass().equals(BadCredentialsException.class)) {
				status = "invalid-password";
			} else if (e.getClass().equals(DisabledException.class)) {
				status = "locked";
			} else if (e.getClass().equals(LockedException.class)) {
				status = "disable";
			} else {
				status = "unknown";
			}
			System.out.println("catch" + status);
		}
	}

	public Account create(Account account) {
		return accountRepository.save(account);
	}

	public Account getAccountfromUsername(String username) {
		Optional<Account> findAccount = accountRepository.findByUsername(username);
		if (!findAccount.isPresent()) {
			// TODO : RsData가 추가되면 F-1을 내보냅니다
			return null;
		}
		return findAccount.get();
	}

	public Optional<Account> findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@SuppressWarnings("checkstyle:WhitespaceAround")
	public Account modifyInfo(Long id, ModifyForm input) {
		Account target = accountRepository.findById(id).orElse(null);
		if (target == null) {
			return null;
		}

		if (canModifyPassword(input)) {
			target.setPassword(input.getPassword());
		}
		if (canModifyNickname(target, input)) {
			target.setNickname(input.getNickname());
		}
		if (canModifyEmail(target, input)) {
			target.setEmail(input.getEmail());
		}
		if (canModifyAddress(target, input)) {
			target.setAddress(input.getAddress());
		}

		return accountRepository.save(target);
	}

	private boolean canModifyNickname(Account target, ModifyForm input) {
		if (input.getNickname().isBlank()) {
			return false;
		}
		if (input.getNickname().equals(target.getNickname())) {
			return false;
		}
		return true;
	}

	private boolean canModifyEmail(Account target, ModifyForm input) {
		if (input.getEmail().isBlank()) {
			return false;
		}
		if (input.getEmail().equals(target.getEmail())) {
			return false;
		}
		return true;
	}

	private boolean canModifyPassword(ModifyForm input) {
		if (input.getPassword().isBlank()) {
			return false;
		}
		if (!input.getPassword().equals(input.getPasswordCheck())) {
			return false;
		}
		return true;
	}

	private boolean canModifyAddress(Account target, ModifyForm input) {
		if (input.getAddress().isBlank()) {
			return false;
		}
		if (input.getAddress().equals(target.getAddress())) {
			return false;
		}
		return true;
	}
}
