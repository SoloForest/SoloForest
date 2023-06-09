package site.soloforest.soloforest.boundedContext.article.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String showList(@PathVariable String type, Model model,
		@RequestParam(value = "page", defaultValue = "0") int page) {
		Page<Share> paging = shareService.getSharesByBoardNumber(type, page);

		if (paging == null) {
			return "redirect:/main";
		}

		model.addAttribute("paging", paging);

		return String.format("article/share/%s", type);
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/{type}/create")
	public String showCreate(@PathVariable String type, Model model) {
		if (!("community".equals(type) || "program".equals(type)))
			return "error/404";

		model.addAttribute("type", type);
		return "article/share/form";
	}

	@Getter
	@AllArgsConstructor
	public static class Form {
		@NotBlank(message = "제목을 입력해주세요.")
		private final String subject;
		@NotBlank(message = "내용을 입력해주세요.")
		private final String content;
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{type}/create")
	public String create(@PathVariable String type, @Valid Form form) {
		Share s;

		if ("community".equals(type))
			s = shareService.create(0, form.getSubject(), form.getContent());
		else if ("program".equals(type))
			s = shareService.create(1, form.getSubject(), form.getContent());
		else
			throw new IllegalArgumentException("Invalid board type: " + type);

		return String.format("redirect:/article/share/detail/%d", s.getId());
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model) {
		Optional<Share> share = shareService.getShare(id);

		if (share.isEmpty()) {
			return "error/404";
		}
		shareService.modifyViewd(share.get());
		model.addAttribute(share.get());

		return "article/share/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String showModify(@PathVariable Long id, Model model) {
		Optional<Share> share = shareService.getShare(id);

		if (share.isEmpty()) {
			return "error/404";
		}
		model.addAttribute(share.get());

		return "article/share/modify";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modify(@PathVariable Long id, @Valid Form form) {
		Optional<Share> share = shareService.getShare(id);

		if (share.isEmpty()) {
			return "error/404";
		}
		shareService.modify(share.get(), form.subject, form.content);

		return String.format("redirect:/article/share/detail/%d", id);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{type}/delete/{id}")
	public String delete(@PathVariable String type, @PathVariable Long id) {
		Share share = shareService.delete(id);

		if (share == null)
			return "error/404";

		return String.format("redirect:/article/share/%s", type);
	}
}