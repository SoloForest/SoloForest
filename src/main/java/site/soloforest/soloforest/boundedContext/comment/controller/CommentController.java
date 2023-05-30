package site.soloforest.soloforest.boundedContext.comment.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

	private static final Logger logger = LoggerFactory.getLogger(CommentController.class);


	@GetMapping("")
	public String showComment(CommentForm commentForm) {
		logger.info("showComment 메서드 호출");
		return "comment/comment";
	}

	@AllArgsConstructor
	@Getter
	@Setter
	public static class CommentForm {
		@NotBlank(message = "내용은 필수로 입력해야 합니다.")
		private String content;
	}

	@PostMapping("/create")
	public String create(@Valid CommentController.CommentForm commentForm) {
		logger.info("create 메서드 호출");
		Comment comment= commentService.create(commentForm.getContent());
		logger.info("댓글 생성: {}", comment);
		return "redirect:/main";
	}

}
