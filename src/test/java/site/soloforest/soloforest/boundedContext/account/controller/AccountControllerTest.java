package site.soloforest.soloforest.boundedContext.account.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class AccountControllerTest {
	@Autowired
	private MockMvc mvc;

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
}
