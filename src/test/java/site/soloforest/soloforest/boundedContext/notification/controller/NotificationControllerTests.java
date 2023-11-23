package site.soloforest.soloforest.boundedContext.notification.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationControllerTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private Rq rq;

	@Test
	@WithUserDetails("usertest")
	@DisplayName("사용자에게 온 알림 조회")
	void t001() throws Exception {
		ResultActions resultActions = mvc.perform(
				get("/notification"))
			.andDo(print());

		resultActions.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				<h5 class="border-bottom my-3 py-2 font-bold">3</h5>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<h5 class="border-bottom my-3 py-2"> 개의 알림이 있습니다.</h5>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				건조증님이 회원님의 댓글에 댓글을 남겼습니다.
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("댓글 알림 보러가기 클릭 시 해당 게시글의 댓글로 이동한다.")
	void t002() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification")
					.param("notificationId", "13")
					.with(csrf()))
			.andDo(print());

		resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/article/*/detail/*"))
			.andExpect(content().string(containsString("""
				<span>건조증</span>
				""".stripIndent().trim())))
			.andExpect(content().string(containsString("""
				<p class="text-lg font-bold"> 넹의 답글</p>
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("admin")
	@DisplayName("신고 알림 보러가기 클릭 시 메인 페이지로 이동한다.")
	void t003() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification")
					.param("notificationId", "12")
					.with(csrf()))
			.andDo(print());

		MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/main*"))
			.andReturn();

		String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
		URI uri = new URI(redirectUrl);
		// 쿼리 파라미터 추출
		String query = uri.getQuery();
		Map<String, String> queryMap = Arrays.stream(query.split("&"))
			// 파라미터 마다 Map으로 추출
			.collect(Collectors.toMap(
				// key : 파라미터 명
				item -> item.split("=")[0],
				// value : 파라미터 값
				item -> URLDecoder.decode(item.split("=")[1], StandardCharsets.UTF_8)
			));

		// msg 파라미터의 값이 신고한 알림 클릭한 내용을 담고있는지 확인
		assertThat(queryMap.get("msg")).contains("신고된 알림을 클릭하여 메인으로 이동합니다.");
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("나에게 온 알림을 삭제할 수 있다.")
	void t004() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification/delete")
					.param("notificationId", "13")
					.with(csrf()))
			.andDo(print());

		resultActions.andExpect(status().is2xxSuccessful())
			// 알림 삭제로 보이지 않는다.
			.andExpect(content().string(not(containsString("""
				건조증님이 회원님의 댓글에 댓글을 남겼습니다.
				""".stripIndent().trim()))));
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("다른 사람에게 간 알림을 삭제할 수 없다.")
	void t005() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification/delete")
					.param("notificationId", "12")
					.with(csrf()))
			.andDo(print());

		MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/main*"))
			.andReturn();

		String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
		URI uri = new URI(redirectUrl);
		// 쿼리 파라미터 추출
		String query = uri.getQuery();
		Map<String, String> queryMap = Arrays.stream(query.split("&"))
			// 파라미터 마다 Map으로 추출
			.collect(Collectors.toMap(
				// key : 파라미터 명
				item -> item.split("=")[0],
				// value : 파라미터 값
				item -> URLDecoder.decode(item.split("=")[1], StandardCharsets.UTF_8)
			));

		// msg 파라미터의 오류 메세지를 담고있는지 확인
		assertThat(queryMap.get("msg")).contains("본인만 삭제할 수 있습니다.");
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("알림 전체 삭제가 가능하다.")
	void t006() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification/deleteAll")
					.param("accountId", "2")
					.with(csrf()))
			.andDo(print());

		MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/notification*"))
			.andReturn();

		String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
		URI uri = new URI(redirectUrl);
		// 쿼리 파라미터 추출
		String query = uri.getQuery();
		Map<String, String> queryMap = Arrays.stream(query.split("&"))
			// 파라미터 마다 Map으로 추출
			.collect(Collectors.toMap(
				// key : 파라미터 명
				item -> item.split("=")[0],
				// value : 파라미터 값
				item -> URLDecoder.decode(item.split("=")[1], StandardCharsets.UTF_8)
			));

		// msg 파라미터의 성공 메세지 확인
		assertThat(queryMap.get("msg")).contains("전체 삭제 되었습니다.");
	}

	@Test
	@WithUserDetails("usertest2")
	@DisplayName("다른 사람의 알림 전체 삭제가 불가능하다.")
	void t007() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/notification/deleteAll")
					.param("accountId", "2")
					.with(csrf()))
			.andDo(print());

		MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/main*"))
			.andReturn();

		String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
		URI uri = new URI(redirectUrl);
		// 쿼리 파라미터 추출
		String query = uri.getQuery();
		Map<String, String> queryMap = Arrays.stream(query.split("&"))
			// 파라미터 마다 Map으로 추출
			.collect(Collectors.toMap(
				// key : 파라미터 명
				item -> item.split("=")[0],
				// value : 파라미터 값
				item -> URLDecoder.decode(item.split("=")[1], StandardCharsets.UTF_8)
			));

		// msg 파라미터의 실패 메세지 확인
		assertThat(queryMap.get("msg")).contains("본인만 알림을 전체 삭제할 수 있습니다.");
	}
}
