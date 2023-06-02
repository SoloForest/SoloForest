package site.soloforest.soloforest.boundedContext.comment.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class CommentServiceTests {
	@Autowired
	private CommentService commentService;

	@Autowired
	private CommentRepository commentRepository;

	@Test
	@DisplayName("댓글 생성 테스트")
	void t01() {
		// 댓글 생성 //
		Comment comment = commentService.create("테스트 댓글");

		// 조회
		Comment comment1 = commentService.findById(1L);
		assertThat(comment.equals(comment1));

	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void t02() {
		// 댓글 생성
		Comment comment = commentService.create("테스트 댓글");

		// 댓글 수정
		Comment comment2 = commentService.modify(comment, "수정!");

		assertThat(comment2.getContent().equals("수정!"));
	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void t03() {
		// 댓글 생성
		Comment comment = commentService.create("테스트 댓글");

		// 댓글 삭제
		commentService.delete(comment);

		comment = commentService.getComment("테스트 댓글");

		assertThat(comment).isNull();
	}


}
