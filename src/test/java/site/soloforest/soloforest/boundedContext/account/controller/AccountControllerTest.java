package site.soloforest.soloforest.boundedContext.account.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import jakarta.servlet.http.HttpSession;
import site.soloforest.soloforest.base.security.CustomAuthenticationSuccessHandler;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AccountControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@BeforeEach
	void initData() {
		Account account = Account.builder()
			.username("testuser")
			.password("test1")
			.nickname("testuser")
			.email("testuser@test.com")
			.build();
		this.accountRepository.save(account);
	}

	@Test
	@DisplayName("로그인 폼 컨트롤러 테스트")
	void t001() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/account/login"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("showLogin"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(view().name("account/login"));
	}

	@Test
	@DisplayName("시큐리티 로그인 테스트 - 로그인 성공")
	void t002() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				post("/account/login")
					.with(csrf())
					.param("username", "testuser")
					.param("password", "test1")
			)
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));

		HttpSession session = resultActions.andReturn().getRequest().getSession(false);// 세션이 없을 경우 새로 만들지 말라는 의미이다.
		SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
		User user = (User)securityContext.getAuthentication().getPrincipal();

		assertThat(user.getUsername()).isEqualTo("testuser");
	}

	@Test
	@DisplayName("시큐리티 로그인 테스트 - 로그인 실패")
	void t003() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				post("/account/login")
					.with(csrf())
					.param("username", "testuser")
					.param("password", "user1")
			)
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/account/login?error=true"));
	}

	@Test
	@DisplayName("회원가입 폼 컨트롤러 테스트")
	void t004() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/account/signUp"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("showSignUp"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(view().name("account/sign_up"));
	}

	@Test
	@DisplayName("시큐리티 회원가입 테스트 - 자동 로그인 성공")
	void t005() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/signUp")
				.with(csrf())
				.param("username", "signUpAndLogin")
				.param("password", "test")
				.param("passwordCheck", "test")
				.param("nickname", "1234")
				.param("email", "signUp@test.com")
				.param("address", "서울시 용산구")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("signup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));

		HttpSession session = resultActions.andReturn().getRequest().getSession(false);// 세션이 없을 경우 새로 만들지 말라는 의미이다.
		SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
		User user = (User)securityContext.getAuthentication().getPrincipal();

		assertThat(user.getUsername()).isEqualTo("signUpAndLogin");
	}

	@Test
	@DisplayName("회원가입 입력 데이터 테스트 - 회원가입 성공 데이터")
	void t006() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/signUp")
				.with(csrf())
				.param("username", "bbosong")
				.param("password", "bbosong")
				.param("passwordCheck", "bbosong")
				.param("nickname", "bbosong")
				.param("email", "bbosong@test.com")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("signup"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));
	}

	@Test
	@DisplayName("회원정보 조회 테스트")
	@WithUserDetails("usertest")
	void t007() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/account/me"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("showMe"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(model().attribute("account", hasProperty("nickname", is("for test"))))
			.andExpect(model().attribute("account", hasProperty("email", is("test@test.com"))));
	}

	@Test
	@DisplayName("회원정보 변경 데이터 테스트 - 비밀번호 변경 실패 데이터")
	@WithUserDetails("usertest")
	void t008() throws Exception {
		String oldPassword = accountRepository.findById(2l).get().getPassword();
		ResultActions resultActions = mvc
			.perform(post("/account/me/2")
				.with(csrf())
				.param("password", "bbosong1")
				.param("passwordCheck", "bbosong2")
				.param("nickname", "for test")
				.param("email", "test@test.com")
				.param("address", "")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("modifyMe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/account/me"));

		assertThat(oldPassword).isEqualTo(accountRepository.findById(2L).get().getPassword());
	}

	@Test
	@DisplayName("회원정보 변경 데이터 테스트 - 비밀번호, 닉네임 변경 성공 데이터")
	@WithUserDetails("usertest")
	void t009() throws Exception {
		String oldPassword = accountRepository.findById(2l).get().getPassword();
		ResultActions resultActions = mvc
			.perform(post("/account/me/2")
				.with(csrf())
				.param("password", "bbosong1")
				.param("passwordCheck", "bbosong1")
				.param("nickname", "somsom")
				.param("email", "test@test.com")
				.param("address", "")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("modifyMe"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/account/me"));

		assertThat(accountRepository.findById(2L).get().getNickname()).isEqualTo("somsom");
		assertThat(oldPassword).isNotEqualTo(accountRepository.findById(2L).get().getPassword());
	}

	@Test
	@DisplayName("회원탈퇴 테스트 - 실패하는 경우")
	@WithUserDetails("usertest")
	void t010() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/withdraw/2")
				.with(csrf())
				.param("password", "bbosong1")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("withdraw"))
			.andExpect(status().isBadRequest());

		assertThat(accountRepository.findById(2L).get().getNickname()).isEqualTo("for test");
	}

	@Test
	@DisplayName("회원탈퇴 테스트 - 성공하는 경우")
	@WithUserDetails("usertest")
	void t011() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/withdraw/2")
				.with(csrf())
				.param("password", "test1")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("withdraw"))
			.andExpect(status().isOk());

		assertThat(accountRepository.findById(2L).get().getNickname()).isEqualTo("알 수 없는 이용자");
		assertThat(accountRepository.findByUsername("usertest")).isEmpty();
	}

	@Test
	@DisplayName("신고하기 테스트 - 신고하면 target의 신고횟수 증가")
	@WithUserDetails("usertest2")
	void t012() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/report/2")
				.with(csrf())
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("report"))
			.andExpect(status().isOk());

		assertThat(accountRepository.findById(2L).get().getReported()).isEqualTo(1);
	}

	@Test
	@DisplayName("신고하기 테스트 - 신고를 3회 받으면 tqrget의 loginRejectedDeadline이 활성화")
	@WithUserDetails("usertest2")
	void t013() throws Exception {
		assertThat(accountRepository.findById(2L).get().getLoginRejectedDeadline()).isNull();

		mvc.perform(post("/account/report/2").with(csrf())).andDo(print());
		mvc.perform(post("/account/report/2").with(csrf())).andDo(print());
		ResultActions resultActions = mvc
			.perform(post("/account/report/2")
				.with(csrf())
			)
			.andDo(print());
		resultActions.andExpect(status().isOk());

		assertThat(accountRepository.findById(2L).get().getReported()).isEqualTo(3);
		assertThat(accountRepository.findById(2L).get().getLoginRejectedDeadline()).isNotNull();
	}

	@Test
	@DisplayName("신고하기 테스트 - loginRejectedDeadline이 활성화되고 로그인하면 403애러")
	void t014() throws Exception {
		Account target = accountRepository.findById(2L).get();
		assertThat(target).isNotNull();

		target.setReported(3);
		target.setLoginRejectedDeadline(LocalDateTime.now().plusDays(3));
		accountRepository.save(target);

		ResultActions resultActions = mvc
			.perform(post("/account/login")
				.with(csrf())
				.param("username", "usertest")
				.param("password", "test1")
			)
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/error/403"));
	}

	@Test
	@DisplayName("계정 찾기 폼 테스트")
	void t015() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/account/find"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("find"))
			.andExpect(status().is2xxSuccessful())
			.andExpect(view().name("account/find_account"));
	}

	@Test
	@DisplayName("username 찾기 테스트")
	void t016() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/find/username")
				.with(csrf())
				.param("email", "test@test.com")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("findUsername"))
			.andExpect(status().is2xxSuccessful()); // 이메일 발송 : username 발송
	}

	@Test
	@DisplayName("password 찾기 테스트")
	void t017() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/find/password")
				.with(csrf())
				.param("email", "test@test.com")
				.param("username", "usertest")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("findPassword"))
			.andExpect(status().is2xxSuccessful()); // 이메일 발송 : 임시 비밀번호 생성 후 발송
	}
}
