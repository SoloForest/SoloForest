package site.soloforest.soloforest.boundedContext.comment.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {

	private final CommentService commentService;

	@GetMapping("")
	public String showComment() {
		return "comment/comment";
	}

	@AllArgsConstructor
	@Getter
	public static class CreateCommentForm {
		@NotBlank(message = "내용은 필수로 입력해야 합니다.")
		private final String content;
	}

	@PostMapping("/create")
	@ResponseBody
	public String create(@Valid CreateCommentForm createCommentForm) {

		commentService.create(createCommentForm.getContent());

		return "1234";
	}

}
