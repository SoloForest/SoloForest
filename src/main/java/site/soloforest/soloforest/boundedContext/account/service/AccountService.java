package site.soloforest.soloforest.boundedContext.account.service;

import java.util.Optional;
import java.util.Random;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.email.EmailDTO;
import site.soloforest.soloforest.base.email.MailSenderRunner;
import site.soloforest.soloforest.base.event.EventReport;
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
	private final MailSenderRunner mailSenderRunner;
	private final TemplateEngine templateEngine;
	private final ApplicationEventPublisher publisher;

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

	public Account getAccountFromUsername(String username) {
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
	public Account modifyInfo(Long id, ModifyForm input, HttpServletRequest request) {
		Account target = accountRepository.findById(id).orElse(null);
		if (target == null) {
			return null;
		}

		if (canModifyPassword(input)) {
			target.setPassword(input.getPassword());
			modifyLoginSetSession(target, input.getPassword(), request);
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

	private void modifyLoginSetSession(Account account, String password, HttpServletRequest request) {
		// 사용자 인증
		UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(
			account.getUsername(),
			password
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

	public ResponseEntity<String> withdraw(Long id, String password, HttpServletRequest request) {
		Account account = accountRepository.findById(id).orElse(null);
		if (account != null) {
			if (passwordEncoder.matches(password, account.getPassword())) {
				account.setUsername(null);
				account.setNickname(null);
				account.setEmail(null);
				account.setAddress(null);
				account.setImageUrl(null);
				account.setDeleted(true);

				accountRepository.save(account);
				logoutAndInvalidateSession(request);
				return new ResponseEntity<>("Account has been successfully deleted", HttpStatus.OK);
			} else {
				return new ResponseEntity<>("Incorrect password", HttpStatus.BAD_REQUEST);
			}
		} else {
			return new ResponseEntity<>("Account does not exist", HttpStatus.NOT_FOUND);
		}
	}

	private void logoutAndInvalidateSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);

		if (session != null) {
			session.invalidate();
		}

		SecurityContextHolder.clearContext();
	}

	public ResponseEntity<String> report(Long targetId) {
		Optional<Account> target = accountRepository.findById(targetId);
		if (target.isEmpty()) {
			return new ResponseEntity<>("대상을 찾을 수 없습니다.", HttpStatus.NOT_FOUND);
		}
		if (target.get().isDeleted()) {
			return new ResponseEntity<>("탈퇴한 이용자에 대한 신고입니다.", HttpStatus.BAD_REQUEST);
		}

		int reported = target.get().reportUp();

		// 알림 객체 생성 이벤트 발생
		publisher.publishEvent(new EventReport(this, target.get()));

		if ((reported / 3 > 0) && (reported % 3 == 0)) {
			target.get().loginReject();
			// target의 session을 끊어줘야 함....
		}

		accountRepository.save(target.get());

		return new ResponseEntity<>("신고가 완료되었습니다.", HttpStatus.OK);
	}

	public String findUsername(String email) {
		Optional<Account> sameEmailAccount = accountRepository.findByEmail(email);
		if (sameEmailAccount.isEmpty()) {
			return "존재하지 않는 회원입니다.";
		}

		sendUsernameEmail(sameEmailAccount.get());

		return "입력된 이메일로 username을 발송했습니다.";
	}

	private void sendUsernameEmail(Account target) {
		Context context = new Context();
		context.setVariable("nickname", target.getNickname());
		context.setVariable("username", target.getUsername());
		String message = templateEngine.process("email/find_username", context);
		EmailDTO emailDTO = EmailDTO.builder()
			.to(target.getEmail())
			.subject("[혼숲] Username 안내")
			.message(message)
			.build();

		mailSenderRunner.sendMessage(emailDTO);
	}

	public String findPassword(String email, String username) {
		Optional<Account> sameEmailAccount = accountRepository.findByEmail(email);
		Optional<Account> sameUsernameAccount = accountRepository.findByUsername(username);

		if (sameEmailAccount.isEmpty() || sameUsernameAccount.isEmpty()) {
			return "존재하지 않는 회원입니다.";
		}
		if (!sameEmailAccount.get().getId().equals(sameUsernameAccount.get().getId())) {
			return "일치하는 계정이 존재하지 않습니다.";
		}

		String temporarPassword = createRandomPassword();
		sameEmailAccount.get().setPassword(temporarPassword);
		accountRepository.save(sameEmailAccount.get());

		sendPasswordEmail(sameEmailAccount.get(), temporarPassword);

		return "입력된 이메일로 임시 비밀번호를 발송했습니다.";
	}

	private void sendPasswordEmail(Account target, String temporarPassword) {
		Context context = new Context();
		context.setVariable("nickname", target.getNickname());
		context.setVariable("password", temporarPassword);
		String message = templateEngine.process("email/find_password", context);
		EmailDTO emailDTO = EmailDTO.builder()
			.to(target.getEmail())
			.subject("[혼숲] Password 안내")
			.message(message)
			.build();

		mailSenderRunner.sendMessage(emailDTO);
	}

	private String createRandomPassword() {
		int leftLimit = 48; // numeral '0'
		int rightLimit = 122; // letter 'z'
		int targetStringLength = 10;
		Random random = new Random();
		String generatedString = random.ints(leftLimit, rightLimit + 1)
			.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
			.limit(targetStringLength)
			.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
			.toString();

		return generatedString;
	}
}