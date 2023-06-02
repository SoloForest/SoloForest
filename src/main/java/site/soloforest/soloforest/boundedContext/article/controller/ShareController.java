package site.soloforest.soloforest.boundedContext.article.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.service.ShareService;

@Controller
@RequestMapping("/article/share")
@RequiredArgsConstructor
public class ShareController {
	private final ShareService shareService;

	@GetMapping("/{type}")
	public String showList(@PathVariable String type, Model model) {
		List<Share> shareList;

		if ("community".equals(type)) {
			shareList = shareService.getSharesByBoardNumber(0);
		} else if ("program".equals(type)) {
			shareList = shareService.getSharesByBoardNumber(1);
		} else {
			return "redirect:/main";
		}

		model.addAttribute("shareList", shareList);

		return String.format("article/share/%s", type);
	}

	@GetMapping("/create")
	public String showCreate() {
		return "article/share/form";
	}

	@Getter
	@AllArgsConstructor
	public static class CreateForm {
		@NotBlank(message = "제목을 입력해주세요.")
		private final String subject;
		@NotBlank(message = "내용을 입력해주세요.")
		private final String content;
	}

	@PostMapping("/create")
	public String create(@Valid CreateForm createForm) {
		shareService.create(createForm.getSubject(), createForm.getContent());

		return "redirect:/article/share/community";
	}
}