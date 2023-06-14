package site.soloforest.soloforest.boundedContext.article.controller;

import java.io.IOException;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.service.ShareService;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Controller
@RequestMapping("/article/share")
@RequiredArgsConstructor
public class ShareController {
	private final Rq rq;
	private final ShareService shareService;

	@GetMapping("/{type}")
	public String showList(@PathVariable String type, Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<Share> paging = shareService.getList(type, kw, page);

		if (paging == null) {
			return "redirect:/main";
		}

		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);

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
	public String create(@PathVariable String type, @Valid Form form,
		@RequestParam(value = "file", defaultValue = "") MultipartFile multipartFile) throws
		IOException {
		RsData<Share> createRsData = shareService.create(type, rq.getAccount(), form.getSubject(), form.getContent(),
			multipartFile);

		if (createRsData.isFail())
			return rq.historyBack(createRsData);

		return rq.redirectWithMsg("/article/share/detail/%d".formatted(createRsData.getData().getId()), createRsData);
	}

	@GetMapping("/detail/{id}")
	public String detail(@PathVariable Long id, Model model, @RequestParam(defaultValue = "0") int page) {
		Optional<Share> share = shareService.findById(id);

		if (share.isEmpty()) {
			return "error/404";
		}

		shareService.modifyViewed(share.get());
		model.addAttribute("article", share.get());

		boolean like = shareService.findLike(share.get(), rq.getAccount());
		model.addAttribute("like", like);

		boolean bookmark = shareService.findBookmark(share.get(), rq.getAccount());
		model.addAttribute("bookmark", bookmark);

		Page<Comment> paging = rq.getPageByArticle(page, share.get());

		model.addAttribute("paging", paging);

		return "article/share/detail";
	}

	@PreAuthorize("isAuthenticated()")
	@GetMapping("/modify/{id}")
	public String showModify(@PathVariable Long id, Model model) {
		Share share = shareService.findById(id).orElse(null);

		RsData<Share> canModifyRsData = shareService.canModify(rq.getAccount(), share);

		if (canModifyRsData.isFail())
			return rq.historyBack(canModifyRsData);

		model.addAttribute(share);

		return "article/share/modify";
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify/{id}")
	public String modify(@PathVariable Long id, @Valid Form form,
		@RequestParam(value = "file", defaultValue = "") MultipartFile multipartFile) throws IOException {
		RsData<Share> modifyRsData = shareService.modify(rq.getAccount(), id, form.subject, form.content,
			multipartFile);

		if (modifyRsData.isFail())
			return rq.historyBack(modifyRsData);

		return rq.redirectWithMsg("/article/share/detail/%d".formatted(modifyRsData.getData().getId()), modifyRsData);
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/{type}/delete/{id}")
	public String delete(@PathVariable String type, @PathVariable Long id) {
		Share share = shareService.findById(id).orElse(null);

		RsData<Share> deleteRsData = shareService.delete(rq.getAccount(), share);

		if (deleteRsData.isFail())
			return rq.historyBack(deleteRsData);

		return rq.redirectWithMsg("/article/share/%s".formatted(type), deleteRsData);
	}

	@AllArgsConstructor
	@Getter
	public static class LikeResponse {
		private boolean success;
		private long newLikeCount;
	}

	@PostMapping("/like")
	public @ResponseBody RsData<LikeResponse> like(Long shareId) {
		boolean success = shareService.like(rq.getAccount(), shareId);

		Optional<Share> share = shareService.findById(shareId);

		if (share.isEmpty()) {
			return RsData.of("F-1", "Fail");
		}

		long newLikeCount = share.get().getLikes();

		return RsData.of("S-1", "Success", new LikeResponse(success, newLikeCount));
	}

	@PostMapping("/bookmark")
	public @ResponseBody boolean bookmark(Long shareId) {
		return shareService.bookmark(rq.getAccount(), shareId);
	}
}