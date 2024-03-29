package site.soloforest.soloforest.boundedContext.comment.service;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.service.ArticleService;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CommentServiceTests {
	@Autowired
	private CommentService commentService;

	@Autowired
	private ArticleService articleService;

	@Autowired
	private AccountService accountService;

	@Test
	@DisplayName("댓글 생성 테스트")
	void t01() {
		Account account = accountService.findByUsername("usertest2").get();
		Article article = articleService.getArticle(2L);

		Comment comment = commentService.create("테스트1234", false, account, article);

		// NotProd 114번 댓글까지 있으므로, 위의 댓글은 115번이어야 함
		Comment findComment = commentService.findById(115L);

		assertThat(comment.equals(findComment)).isTrue();
	}

	@SuppressWarnings("checkstyle:RegexpMultiline")
	@Test
	@DisplayName("댓글 수정 테스트")
	void t02() {
		Account account = accountService.findByUsername("usertest2").get();
		Article article = articleService.getArticle(2L);
		Comment comment = commentService.getComment(5L);

		Comment modifyComment = commentService.modify(comment, "가시죠", false);
		// 내용 변경됐으니 같으면 안됨!
		assertThat(comment.equals(modifyComment)).isFalse();
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void t03() {
		Comment comment = commentService.getComment(5L);

		commentService.delete(comment);

		// 삭제된 댓글 가져오기
		Comment deletedComment = commentService.getComment(5L);

		// 댓글이 null인지 확인
		assertNull(deletedComment, "댓글이 삭제되었어야 합니다.");

	}

	@Test
	@DisplayName("부모 댓글이 삭제되고, 대댓글이 1개일 때 대댓글이 삭제될 경우 부모 댓글 삭제 테스트")
	void t04() {
		Comment comment = commentService.getComment(16L);

		// 부모 댓글 먼저 지우기
		commentService.delete(comment);

		// 댓글 삭제 확인(자식 댓글이 있으니, 객체 삭제처리 되지 않고 isDeleted 속성만 변경
		Comment deletedComment = commentService.getComment(16L);

		// 댓글 상태가 삭제로 변경되었는지 확인
		assertThat(deletedComment.isDeleted() == true);

		// 자식 댓글 삭제(16번의 댓글의 대댓글 id는 17번)
		Comment delComment = comment.getChildren().get(0);
		commentService.delete(delComment);

		// 삭제 확인용 객체 가져오기 -> 다 Null이어야 함
		Comment assertParrent = commentService.getComment(16L);
		Comment assertChild = commentService.getComment(17L);

		// Null인지 확인
		assertNull(assertParrent);
		assertNull(assertChild);
	}

}
