package site.soloforest.soloforest.boundedContext.comment.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.ui.Model;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.Value;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	// private final ArticleService articleService;
	private final CommentService commentService;

	// 디버깅시 활용
	// private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	// 	logger.info("showComment 메서드 호출");

	// 삭제 예정(댓글 자체는 게시글 조회시 자동으로 나오게)
	@GetMapping("")
	public String showComment(CommentForm commentForm) {
		return "comment/comment";
	}

	@AllArgsConstructor
	@Getter
	@Setter
	public static class CommentForm {
		@NotBlank(message = "내용은 필수로 입력해야 합니다.")
		private String content;

		// 기본값 false로 설정
		private Boolean secret = false;
	}

	// @PreAuthorize("isAuthenticated()")
	// ToDO : 게시글 id를 통해 게시글을 얻고, 현재 로그인한 회원의 사용자 정보도 얻어서 등록한다.
	// ToDo : 대댓글 방식은 PostMapping을 추가하고, 댓글 id를 가지고 build를 하면 될 것 같은데 좀 더 고민해보기!
	@PostMapping("/create/{id}")
	public String create(Model model, @PathVariable("id") Long id, @Valid CommentForm commentForm, Principal principal) {

		// 게시글 id를 가져오고, 현재 로그인한 회원의 정보, 댓글 폼에 입력한 내용으로 댓글 객체 생성
		Article article = articleService.getArticle(id);
		Account account = AccoutService.getUser(principal.getName());
		Comment comment = commentService.create(commentForm, account);

		Comment comment= commentService.create(commentForm, account, article);

		return "redirect:/main";

		// TODO : 댓글 작성 시 게시글로 리다이렉트
		// return "redirect:/article/detail/%s".formatted(id);
	}

	// @PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}") 	// 댓글id
	public String showModify(CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
		Comment comment = this.commentService.getComment(id);
		if (!comment.getWriter().getUsername().equals(principal.getName())) { // 댓글 작성한 회원정보와 일치 여부 확인
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
		}
		commentForm.setContent(comment.getContent());
		// 비밀 댓글 여부도 변경했을 수 있으니 비밀 댓글 속성도 값 가져오도록 수정
		commentForm.setSecret(comment.getSecret());
		// TODO : 다시 게시글 정보로 돌아가는 리다이렉트로 수정
		return "comment/comment";// 댓글 폼 html
	}

	// @PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}") 	// 댓글 id
	public String modify(@Valid CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
		Comment comment = commentService.getComment(id);

		if(!comment.getWriter().getUsername().equals(principal.getName()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");

		// 비밀 댓글 여부 변경할 수 있으니 입력받는 객체 자체를 넘기기로 변경
		commentService.modify(comment, commentForm);
		// TODO : 게시글 정보로 리다이렉트 변경
		return "redirect:/main";
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
		return "comment/comment";
	}

}
