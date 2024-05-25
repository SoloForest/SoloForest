package site.soloforest.soloforest.boundedContext.account.service;

import java.io.IOException;
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
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.account.dto.AccountDTO;
import site.soloforest.soloforest.boundedContext.account.dto.ModifyForm;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;
import site.soloforest.soloforest.boundedContext.picture.entity.Picture;
import site.soloforest.soloforest.boundedContext.picture.pictureHandler.PictureHandler;
import site.soloforest.soloforest.boundedContext.picture.repository.PictureRepository;

@Service
@RequiredArgsConstructor
public class AccountService {
	private final AccountRepository accountRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final MailSenderRunner mailSenderRunner;
	private final TemplateEngine templateEngine;
	private final ApplicationEventPublisher publisher;
	private final PictureHandler pictureHandler;
	private final PictureRepository pictureRepository;

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
		return accountRepository.findByUsername(username).orElse(null);
	}

	public Optional<Account> findByUsername(String username) {
		return accountRepository.findByUsername(username);
	}

	@SuppressWarnings("checkstyle:WhitespaceAround")
	public RsData<Account> modifyInfo(Long id, ModifyForm input, HttpServletRequest request) throws IOException {
		Account target = accountRepository.findById(id).orElse(null);
		if (target == null) {
			return RsData.of("F-1", "대상을 찾을 수 없습니다.");
		}
		RsData<Account> result;

		/* 프로필 이미지 변경 */
		result = canModifyPicture(target, input);
		if (result.isSuccess()) {
			// 사진 업로드
			RsData<Picture> picture = pictureHandler.parseFileInfo(input.getFile());
			if (picture.isSuccess()) {
				if (target.getPicture() != null) {
					Picture oldPicture = target.getPicture();
					target.setPicture(null);
					accountRepository.save(target);
					pictureRepository.delete(oldPicture);
				}
				target.setPicture(picture.getData());

			} else { // 사진 업로드 실패할 경우 알림 출력
				String pictureResultCode = picture.getResultCode();
				String pictureMsg = picture.getMsg();
				return RsData.of(pictureResultCode, pictureMsg);
			}
		} else if (!result.getResultCode().equals("F-1")) {
			return result;
		}

		/* 비밀번호 변경 */
		result = canModifyPassword(input);
		if (result.isSuccess()) {
			target.setPassword(input.getPassword());
			modifyLoginSetSession(target, input.getPassword(), request);
		} else if (!result.getResultCode().equals("F-1")) {
			return result;
		}

		/* 닉네임 변경 */
		result = canModifyNickname(target, input);
		if (result.isSuccess()) {
			target.setNickname(input.getNickname());
		} else if (!result.getResultCode().equals("F-1")) {
			return result;
		}

		/* 이메일 변경 */
		result = canModifyEmail(target, input);
		if (result.isSuccess()) {
			target.setEmail(input.getEmail());
		} else if (!result.getResultCode().equals("F-1")) {
			return result;
		}

		/* 주소지 변경 */
		result = canModifyAddress(target, input);
		if (result.isSuccess()) {
			target.setAddress(input.getAddress());
		} else if (!result.getResultCode().equals("F-1")) {
			return result;
		}

		return RsData.of("S-1", "계정정보가 수정되었습니다.", accountRepository.save(target));
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

	private RsData<Account> canModifyPicture(Account target, ModifyForm input) {
		if (input.getFile() == null || input.getFile().isEmpty()) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		return RsData.of("S-1", "프로필 사진을 변경할 수 있습니다.");
	}

	private RsData<Account> canModifyNickname(Account target, ModifyForm input) {
		if (input.getNickname().isBlank()) {
			return RsData.of("F-2", "닉네임이 반드시 입력되어야 합니다.");
		}
		if (input.getNickname().equals(target.getNickname())) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		if (accountRepository.findByNickname(input.getNickname()).isPresent()) {
			return RsData.of("F-3", "이미 존재하는 닉네임입니다.");
		}
		return RsData.of("S-1", "닉네임을 변경할 수 있습니다.");
	}

	private RsData<Account> canModifyEmail(Account target, ModifyForm input) {
		if (input.getEmail().isBlank()) {
			return RsData.of("F-2", "이메일이 반드시 입력되어야 합니다.");
		}
		if (input.getEmail().equals(target.getEmail())) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		if (accountRepository.findByEmail(input.getEmail()).isPresent()) {
			return RsData.of("F-3", "이미 존재하는 이메일입니다.");
		}
		return RsData.of("S-1", "이메일을 변경할 수 있습니다.");
	}

	private RsData<Account> canModifyPassword(ModifyForm input) {
		if (input.getPassword().isBlank()) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		if (!input.getPassword().equals(input.getPasswordCheck())) {
			return RsData.of("F-2", "동일한 비밀번호가 입력되어야 합니다.");
		}
		return RsData.of("S-1", "비밀번호를 변경할 수 있습니다.");
	}

	private RsData<Account> canModifyAddress(Account target, ModifyForm input) {
		if (input.getAddress().isBlank()) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		if (input.getAddress().equals(target.getAddress())) {
			return RsData.of("F-1", "변경 사항이 없습니다.");
		}
		return RsData.of("S-1", "주소를 변경할 수 있습니다.");
	}

	public ResponseEntity<String> withdraw(Long id, String password, HttpServletRequest request) {
		Account account = accountRepository.findById(id).orElse(null);
		if (account != null) {
			if (passwordEncoder.matches(password, account.getPassword())) {
				account.setUsername(null);
				account.setNickname(null);
				account.setEmail(null);
				account.setAddress(null);
				account.setPicture(null);
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
		// 테스트를 위해 @test.com인 이메일은 발송하지 않음
		if (target.getEmail().endsWith("@test.com")) {
			return;
		}
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

		if (sameEmailAccount.get().getEmail().endsWith("@test.com")) {
			return "테스트용 계정 이메일 발송하지 않음";
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

	// notification에서 객체 정보 가져오기용
	public Account findById(Long accountId) {
		return accountRepository.findById(accountId).get();
	}
}