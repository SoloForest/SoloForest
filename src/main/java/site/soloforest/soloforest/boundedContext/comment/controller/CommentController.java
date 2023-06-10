package site.soloforest.soloforest.boundedContext.comment.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.service.ArticleService;
import site.soloforest.soloforest.boundedContext.comment.dto.CommentDTO;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	// TODO : Rq 도입시 변경
	 private final AccountService accountService;
	private final CommentService commentService;

	private final ArticleService articleService;

	// 디버깅시 활용
	// private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	// 	logger.info("showComment 메서드 호출");

	// 삭제 예정(댓글 자체는 게시글 조회시 자동으로 나오게)
	@GetMapping("")
	public String showComment(Model model) {

		Article article = articleService.getArticle(1L);
		List<Comment> commentList = commentService.getCommentList(article);

		model.addAttribute("article", article);
		model.addAttribute("commentList", commentList);

		return "comment/comment";
	}
	// ToDO : 게시글 id를 통해 게시글을 얻고, 현재 로그인한 회원의 사용자 정보도 얻어서 등록한다.

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(Model model ,@ModelAttribute CommentDTO commentDTO, Principal principal) {
		Article article = articleService.getArticle(commentDTO.getArticleId());
		Account account = accountService.findByUsername(principal.getName());

		if (commentDTO.getParentId() == null) {
			// 부모 댓글 생성
			commentService.create(commentDTO.getCommentContents(), commentDTO.getSecret(), account, article);
		}
		model.addAttribute("article", article);

		List<Comment> commentList = commentService.getCommentList(article);
		model.addAttribute("commentList", commentList);

		return "comment/comment :: #comment-list";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/reply/create")
	public String replyCreate(Model model ,@ModelAttribute CommentDTO commentDTO, Principal principal) {
		Article article = articleService.getArticle(commentDTO.getArticleId());
		Account account = accountService.findByUsername(principal.getName());

		// 자식 댓글 생성
			// 부모 댓글 찾아오기
			Comment parent = commentService.getComment(commentDTO.getParentId());
			// 자식 댓글 생성
			commentService.createReplyComment(commentDTO.getCommentContents(), commentDTO.getSecret(), account, article, parent);

		model.addAttribute("article", article);

		List<Comment> commentList = commentService.getCommentList(article);
		model.addAttribute("commentList", commentList);

		return "comment/comment :: #comment-list";
	}

	// 답글 수정 + 댓글 수정 둘다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify")
	public String modify(CommentDTO commentDTO, Principal principal, Model model) {
		Article article = articleService.getArticle(commentDTO.getArticleId());
		Account account = accountService.findByUsername(principal.getName());

		// ToDo : RsData 변경 필요 + Admin 사용자 수정 가능
		 if(account.getId() != commentDTO.getCommentWriter()) {
			 throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
		 }

		 // 대댓글(답글)도 id로 찾을 수 있음(댓글과 동일 객체 사용)
		Comment comment = commentService.getComment(commentDTO.getId());

		// 댓글 내용, 비밀 댓글 여부만 수정 할테니 해당 값 넘기기
		commentService.modify(comment, commentDTO.getCommentContents().trim(), commentDTO.getSecret());

		model.addAttribute("article", article);
		List<Comment> commentList = commentService.getCommentList(article);
		model.addAttribute("commentList", commentList);

		return "comment/comment :: #comment-list";
	}

	// 댓글 삭제 메서드
	// @PreAuthorize("isAuthenticated()") // 로그인한 사용자만보임
	@DeleteMapping("/{id}")
	public String delete(Principal principal, @PathVariable("id") Long id) {
		Comment comment = this.commentService.getComment(id);
		if (!comment.getWriter().getUsername().equals(principal.getName())) {
			// 댓글 작성한 회원정보와 일치 여부 확인
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제권한이 없습니다");
		}
		commentService.delete(comment);
		// TODO : 게시글 정보로 리다이렉트
		return "comment/comment";
	}

}
