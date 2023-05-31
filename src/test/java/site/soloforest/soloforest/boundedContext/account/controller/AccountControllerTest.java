package site.soloforest.soloforest.boundedContext.account.controller;

import static org.assertj.core.api.Assertions.*;
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
import org.springframework.security.crypto.password.PasswordEncoder;
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
	@Autowired
	private PasswordEncoder passwordEncoder;

	@BeforeEach
	void initData() {
		Account account = Account.builder()
			.username("usertest")
			.password(passwordEncoder.encode("test1"))
			.nickname("for test")
			.email("test@test.com")
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
			.andExpect(view().name("/account/login"));
	}

	@Test
	@DisplayName("시큐리티 로그인 테스트 - 로그인 성공")
	void t002() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				post("/account/login")
					.with(csrf())
					.param("username", "usertest")
					.param("password", "test1")
			)
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));

		HttpSession session = resultActions.andReturn().getRequest().getSession(false);// 세션이 없을 경우 새로 만들지 말라는 의미이다.
		SecurityContext securityContext = (SecurityContext)session.getAttribute("SPRING_SECURITY_CONTEXT");
		User user = (User)securityContext.getAuthentication().getPrincipal();

		assertThat(user.getUsername()).isEqualTo("usertest");
	}

	@Test
	@DisplayName("시큐리티 로그인 테스트 - 로그인 실패")
	void t003() throws Exception {
		ResultActions resultActions = mvc
			.perform(
				post("/account/login")
					.with(csrf())
					.param("username", "usertest")
					.param("password", "user1")
			)
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/account/login?error=true"));
	}
}
