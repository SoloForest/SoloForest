package site.soloforest.soloforest.boundedContext.comment.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	private final CommentService commentService;

	// 디버깅시 활용
	// private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	// 	logger.info("showComment 메서드 호출");

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
	}

	// @PreAuthorize("isAuthenticated()")
	// user의 Username 가져와서 작성자 정보도 업로드 수정
	@PostMapping("/create")
	public String create(@Valid CommentForm commentForm) {
		Comment comment= commentService.create(commentForm.getContent());
		return "redirect:/main";
	}

	// @PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}") 	// 댓글id
	public String showModify(CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
		Comment comment = this.commentService.getComment(id);
		if (!comment.getWriter().getUsername().equals(principal.getName())) { // 댓글 작성한 회원정보와 일치 여부 확인
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
		}
		commentForm.setContent(comment.getContent());
		return "comment/comment";// 댓글 폼 html
	}

	// @PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}") 	// 댓글 id
	public String modify(@Valid CommentForm commentForm, @PathVariable("id") Long id, Principal principal) {
		Comment comment = commentService.getComment(id);

		if(!comment.getWriter().getUsername().equals(principal.getName()))
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");

		commentService.modify(comment, commentForm.getContent());
		return "redirect:/main";
	}

	// 댓글 삭제 메서드
	// @PreAuthorize("isAuthenticated()") // 로그인한 사용자만보임
	@GetMapping("/delete/{id}")
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
