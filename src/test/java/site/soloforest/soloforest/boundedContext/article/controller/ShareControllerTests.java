package site.soloforest.soloforest.boundedContext.article.controller;

import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
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

import site.soloforest.soloforest.boundedContext.article.service.ShareService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class ShareControllerTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ShareService shareService;

	@Test
	@DisplayName("커뮤니티 게시판 조회 테스트")
	void t001() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/article/share/community"))
			.andDo(print());

		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(view().name("article/share/community"))
			.andExpect(model().attributeExists("shareList"));
	}

	@Test
	@DisplayName("프로그램 게시판 조회 테스트")
	void t002() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/article/share/program"))
			.andDo(print());

		resultActions
			.andExpect(status().is2xxSuccessful())
			.andExpect(view().name("article/share/program"))
			.andExpect(model().attributeExists("shareList"));
	}

	@Test
	@DisplayName("잘못된 경로 - 메인 페이지로 리다이렉트 테스트")
	void t003() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/article/share/invalid"))
			.andDo(print());

		resultActions
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/main"));
	}

	@Test
	@DisplayName("게시글 작성 폼 처리 테스트")
	void t004() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/article/share/community/create")
				.with(csrf())
				.param("subject", "test Subject")
				.param("content", "test Content")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(ShareController.class))
			.andExpect(handler().methodName("create"))
			.andExpect(redirectedUrlPattern("/article/share/detail/*"));
	}

	@Test
	@DisplayName("게시글 상세 페이지 잘못 된 경로 - 에러 페이지 호출 테스트")
	void t005() throws Exception {
		ResultActions resultActions = mvc
			.perform(get("/article/share/detail/-1"))
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(ShareController.class))
			.andExpect(handler().methodName("detail"))
			.andExpect(content().string(containsString("404 ERROR")));
	}

	@Test
	@DisplayName("게시글 수정 폼 처리 테스트")
	void t006() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/article/share/modify/1")
				.with(csrf())
				.param("subject", "test Subject")
				.param("content", "test Content")
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(ShareController.class))
			.andExpect(handler().methodName("modify"))
			.andExpect(redirectedUrl("/article/share/detail/1"));
	}
}