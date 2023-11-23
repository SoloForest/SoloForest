package site.soloforest.soloforest.boundedContext.comment.controller;

import static org.assertj.core.api.Assertions.*;
import static org.hamcrest.Matchers.not;
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
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CommentControllerTests {

	@Autowired
	private MockMvc mvc;

	@Autowired
	private CommentService commentService;

	@Test
	@DisplayName("게시물 상세 페이지에 접속하면 댓글이 나타난다.")
	void t001() throws Exception {
		ResultActions resultActions = mvc.perform(get("/article/share/detail/1")).andDo(print());

		resultActions.andExpect(status().is2xxSuccessful()).andExpect(content().string(containsString("""
			테스트 데이터 5
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("게시물 댓글 생성 테스트 - 내용을 입력하지 않으면 게시글로 재이동된다.")
	void t002() throws Exception {
		ResultActions resultActions = mvc.perform(post("/comment/create").with(csrf()).param("articleId", "1"))
			.andDo(print());

		resultActions.andExpect(status().is3xxRedirection()).andExpect(redirectedUrlPattern("**/article/*/detail/*"));
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("게시물 댓글 생성 테스트")
	void t003() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/comment/create").with(csrf()).param("articleId", "1").param("commentContents", "이거 생기나?"))
			.andDo(print());

		MvcResult mvcResult = resultActions.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrlPattern("/article/*/detail/*"))
			.andReturn();

		String redirectUrl = mvcResult.getResponse().getRedirectedUrl();
		String commentId = redirectUrl.substring(redirectUrl.lastIndexOf("_") + 1);

		Comment comment = commentService.getComment(Long.parseLong(commentId));
		assertThat(comment.getContent()).isEqualTo("이거 생기나?");
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("게시물 대댓글 생성 테스트")
	void t004() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/comment/reply/create")
					.with(csrf()).param("articleId", "1")
					.param("parentId", "2")
					.param("commentContents", "대댓글 생기나?"))
			.andDo(print());

		resultActions.andExpect(status().is2xxSuccessful())
			.andExpect(content().string(containsString("""
				대댓글 생기나?
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("usertest")
	@DisplayName("게시물 댓글 수정 테스트 - 본인이 작성하지 않은 댓글은 관리자가 아닌 이상 수정할 수 없다")
	void t005() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/comment/modify")
					.with(csrf()).param("articleId", "1")
					.param("commentWriter", "4")
					.param("commentContents", "수정 되나?")
					.param("Id", "3"))
			.andDo(print());

		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("수정권한이 없습니다"));
	}

	@Test
	@WithUserDetails("admin")
	@DisplayName("게시물 댓글 수정 테스트 - 관리자는 본인이 작성한 댓글이 아니여도 수정할 수 있다")
	void t006() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/comment/modify")
					.with(csrf()).param("articleId", "1")
					.param("commentWriter", "4")
					.param("commentContents", "수정 되나?")
					.param("Id", "3"))
			.andDo(print());

		resultActions
			.andExpect(status().is2xxSuccessful())
			// 기존 내용은 없어지고
			.andExpect(content().string(not(containsString("""
				누가 짱나게 하면 어떻게 하죠?
				""".stripIndent().trim()))))
			// 변경 내용
			.andExpect(content().string(containsString("""
				수정 되나?
				""".stripIndent().trim())));
	}

	@Test
	@WithUserDetails("admin")
	@DisplayName("게시물 댓글 수정 테스트 - 관리자는 본인이 작성한 댓글이 아니여도 수정할 수 있다")
	void t007() throws Exception {
		ResultActions resultActions = mvc.perform(
				post("/comment/modify")
					.with(csrf()).param("articleId", "1")
					.param("commentWriter", "4")
					.param("commentContents", "수정 되나?")
					.param("Id", "3"))
			.andDo(print());

		resultActions
			.andExpect(status().is2xxSuccessful())
			// 기존 내용은 없어지고
			.andExpect(content().string(not(containsString("""
				누가 짱나게 하면 어떻게 하죠?
				""".stripIndent().trim()))))
			// 변경 내용
			.andExpect(content().string(containsString("""
				수정 되나?
				""".stripIndent().trim())));
	}

	/*
		댓글 삭제
		- 본인이 작성한 댓글만 삭제할 수 있다
		- 관리자는 본인이 작성하지 않은 댓글도 삭제할 수 있다.
		- 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록 ("삭제된 댓글입니다" 출력)
		- 삭제 하려는 대댓글의 부모 댓글이 삭제 상태가 아닌 경우 : 대댓글 자체 삭제
		- 삭제 하려는 댓글의 자식 댓글이 없는 경우 : 댓글 자체 삭제
		- 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨
	 */
	@Test
	@WithUserDetails("usertest")
	@DisplayName("게시물 댓글 삭제 테스트 - 관리자가 아닌 경우 본인이 작성한 댓글만 삭제할 수 있다")
	void t008() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "2")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().isBadRequest())
			.andExpect(status().reason("삭제 권한이 없습니다"));
	}

	@Test
	@WithUserDetails("admin")
	@DisplayName("게시물 댓글 삭제 테스트 - 관리자는 본인이 작성하지 않은 댓글도 삭제할 수 있다")
	void t09() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "2")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(status().is2xxSuccessful())
			// 기존 내용은 없어짐
			.andExpect(content().string(not(containsString("""
				넵
				""".stripIndent().trim()))));
	}

	@Test
	@WithUserDetails("usertest3")
	@DisplayName("게시물 댓글 삭제 테스트 - 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록 ('삭제된 댓글입니다') 출력)")
	void t010() throws Exception {
		// 삭제 하려는 댓글의 대댓글이 있을 경우 : 내용 안보이도록
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "3")
				.param("commentWriter", "4")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			// 기존 내용이 안보여지고
			.andExpect(content().string(not(containsString("""
				<p class="text-lg font-bold">누가 짱나게 하면 어떻게 하죠?</p>
				          """.stripIndent().trim()))))
			// 새로운 내용이 보여짐
			.andExpect(content().string(containsString("""
				삭제된 댓글입니다.
				          """.stripIndent().trim())));
	}

	@Test
	@WithUserDetails("usertest2")
	@DisplayName("게시물 댓글 삭제 테스트 - 삭제 하려는 대댓글의 부모 댓글이 삭제 상태가 아닌 경우 : 대댓글 자체 삭제")
	void t011() throws Exception {
		// 삭제 하려는 대댓글의 부모 댓글이 삭제 상태가 아닌 경우 : 대댓글 자체 삭제
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "114")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			// 기존 내용이 없어짐
			.andExpect(content().string(not(containsString("""
				<p class="text-lg font-bold">넹의 답글</p>
				          """.stripIndent().trim()))));

		Comment comment = commentService.getComment(114L);
		// 실제 삭제되었어야함
		assertThat(comment).isNull();
	}

	@Test
	@WithUserDetails("usertest2")
	@DisplayName("게시물 댓글 삭제 테스트 - 삭제 하려는 댓글의 자식 댓글이 없는 경우 : 댓글 자체 삭제")
	void t012() throws Exception {
		// 삭제 하려는 댓글의 자식 댓글이 없는 경우 : 댓글 자체 삭제
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "2")
				.param("commentWriter", "3")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			// 기존 내용이 없어짐
			.andExpect(content().string(not(containsString("""
				<p class="text-lg font-bold">넵</p>
				          """.stripIndent().trim()))));

		Comment comment = commentService.getComment(2L);
		// 실제 삭제되었어야함
		assertThat(comment).isNull();
	}

	@Test
	@WithUserDetails("admin")
	@DisplayName("게시물 댓글 삭제 테스트 - 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨")
	void t013() throws Exception {
		// 부모 댓글 삭제인 상황에서 자식 댓글 삭제 : 부모 댓글 객체도 같이 삭제됨
		ResultActions resultActions = mvc
			.perform(post("/comment/delete")
				.with(csrf()) // CSRF 키 생성
				.param("articleId", "1")
				.param("id", "17") // 16(부모댓글 - 삭제 상태) - 17 자식 댓글 => 17번 삭제 시 부모도 삭제
				.param("commentWriter", "1")
			)
			.andDo(print());

		// THEN
		resultActions
			.andExpect(handler().handlerType(CommentController.class))
			.andExpect(handler().methodName("delete"))
			.andExpect(status().is2xxSuccessful())
			// 기존 내용이 없어짐
			.andExpect(content().string(not(containsString("""
				<p class="text-lg font-bold">이녀석 삭제되면 위에 녀석 삭제되어야 함</p>
				          """.stripIndent().trim()))));

		Comment son = commentService.getComment(17L);
		Comment parent = commentService.getComment(16L);
		// 실제 삭제되었어야함
		assertThat(son).isNull();
		assertThat(parent).isNull();
	}
}
