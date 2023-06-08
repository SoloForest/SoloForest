package site.soloforest.soloforest.boundedContext.account.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
}
