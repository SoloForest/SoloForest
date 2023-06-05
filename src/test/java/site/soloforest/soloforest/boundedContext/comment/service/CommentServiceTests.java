package site.soloforest.soloforest.boundedContext.comment.service;

import static org.assertj.core.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.service.ShareService;
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
	private ShareService shareService;

	@Autowired
	private AccountRepository accountRepository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	// @BeforeEach
	// void initData() {
	// 	// 회원 생성
	// 	Account account = Account.builder()
	// 		.username("usertest")
	// 		.password(passwordEncoder.encode("test1"))
	// 		.nickname("for test")
	// 		.email("test@test.com")
	// 		.build();
	// 	this.accountRepository.save(account);
	//
	// 	// 게시글 작성
	//
	// 	Article tmp1 = Share.builder()
	// 		.boardNumber(0)
	// 		.subject("테스트제목1")
	// 		.content("테스트내용1")
	// 		.account(account)
	// 		.build();
	//
	// 	// 댓글 작성
	// 	Comment comment1 = commentService.create("테스트댓글1", false, account, tmp1);
	// 	Comment comment2 = commentService.create("테스트댓글2", false, account, tmp1);
	// 	Comment comment3 = commentService.create("테스트댓글3", false, account, tmp1);
	// 	Comment comment4 = commentService.create("테스트댓글4", false, account, tmp1);
	// 	Comment comment5 = commentService.create("테스트댓글5", false, account, tmp1);
	// 	Comment comment6 = commentService.create("테스트댓글6", false, account, tmp1);
	//
	// }

	// TODO : NotProd 생성 시 테스트케이스 작성
	@Test
	@DisplayName("댓글 생성 테스트")
	void t01() {

	}

	@Test
	@DisplayName("댓글 수정 테스트")
	void t02() {

	}

	@Test
	@DisplayName("댓글 삭제 테스트")
	void t03() {

	}


}
