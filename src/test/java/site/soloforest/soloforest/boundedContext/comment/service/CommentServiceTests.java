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
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.service.ShareService;
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
	private ShareService shareService;

	@Autowired
	private AccountRepository accountRepository;

	@Test
	@DisplayName("댓글 생성 테스트")
	void t01() {
		Account account = accountRepository.findByUsername("usertest2").get();
		Share article = shareService.getShare(2L).get();

		Comment comment = commentService.create("테스트1234", false, account, article);

		// NotProd 13번 댓글까지 있으므로, 위의 댓글은 14번이어야 함
		Comment findComment = commentService.findById(14L);

		assertThat(comment.equals(findComment)).isTrue();
	}

	@SuppressWarnings("checkstyle:RegexpMultiline")
	@Test
	@DisplayName("댓글 수정 테스트")
	void t02() {
		Account account = accountRepository.findByUsername("usertest2").get();
		Share article = shareService.getShare(2L).get();
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
		Comment comment = commentService.getComment(3L);

		// 부모 댓글 먼저 지우기
		commentService.delete(comment);

		// 댓글 삭제 확인(자식 댓글이 있으니, 내용만 "삭제되었습니다"
		Comment deletedComment = commentService.getComment(3L);

		// 댓글 내용이 "삭제되었습니다"로 바뀐지 확인
		assertThat(deletedComment.getContent().equals("삭제되었습니다"));

		// 자식 댓글 삭제(3번의 댓글의 대댓글 id는 12번)
		Comment delComment = comment.getChildren().get(0);
		commentService.delete(delComment);

		// 삭제 확인용 객체 가져오기 -> 다 Null이어야 함
		Comment assertParrent = commentService.getComment(3L);
		Comment assertChild = commentService.getComment(12L);

		// Null인지 확인
		assertNull(assertParrent);
		assertNull(assertChild);
	}

}
