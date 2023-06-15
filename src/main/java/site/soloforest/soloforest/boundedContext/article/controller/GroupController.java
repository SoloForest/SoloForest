package site.soloforest.soloforest.boundedContext.article.controller;

import java.time.LocalDateTime;

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

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.groupForm;
import site.soloforest.soloforest.boundedContext.article.service.GroupService;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;

@Controller
@RequestMapping("/article/group")
@RequiredArgsConstructor
public class GroupController {
	private final GroupService groupService;
	private final Rq rq;

	@GetMapping("/detail/{id}")
	public String detail(Model model, @PathVariable("id") Long id, @RequestParam(defaultValue = "0") int page) {
		Group group = groupService.getGroup(id);
		groupService.modifyViewed(group);
		model.addAttribute("article", group);

		boolean like = groupService.alreadyLiked(group, rq.getAccount());
		model.addAttribute("like", like);

		boolean bookmark = groupService.alreadyBookmarked(group, rq.getAccount());
		model.addAttribute("bookmark", bookmark);

		Page<Comment> paging = rq.getPageByArticle(page, group);

		model.addAttribute("paging", paging);
		return "article/group/detail";
	}

	@GetMapping("/create")
	@PreAuthorize("isAuthenticated()")
	public String createget(Model model) {
		model.addAttribute("groupForm", new groupForm());

		return "article/group/write";
	}

	@PostMapping("/create")
	@PreAuthorize("isAuthenticated()")
	public String create(String subject, String content, int member, LocalDateTime startDate, LocalDateTime endDate,
		int money, String location) {

		Group group = groupService.create(subject, content, member, startDate, endDate, money, location,
			rq.getAccount());

		return "redirect:/article/group/detail/%d".formatted(group.getId());
	}

	@GetMapping("/modify/{id}")
	@PreAuthorize("isAuthenticated()")
	public String modifyget(Model model, @PathVariable("id") Long id) {
		Group group = groupService.getGroup(id);

		if (!rq.getAccount().getId().equals(group.getAccount().getId())) {
			return "redirect:/error/403";
		}

		model.addAttribute("groupForm", group);
		return "article/group/write";
	}

	@PostMapping("/modify/{id}")
	@PreAuthorize("isAuthenticated()")
	public String modify(@PathVariable("id") Long id,
		String subject, String content) {
		Group group = groupService.getGroup(id);

		if (!rq.getAccount().getId().equals(group.getAccount().getId())) {
			return "redirect:/error/403";
		}

		groupService.modify(group, subject, content);
		return "redirect:/article/group/detail/%d".formatted(id);
	}

	@GetMapping("/delete/{id}")
	@PreAuthorize("isAuthenticated()")
	public String delete(@PathVariable("id") Long id) {
		Group group = groupService.getGroup(id);

		if (!rq.getAccount().getId().equals(group.getAccount().getId())) {
			return "redirect:/error/403";
		}

		groupService.delete(group);
		return "redirect:/article/group/list";
	}

	@GetMapping("/list")
	public String showList(Model model,
		@RequestParam(value = "page", defaultValue = "0") int page,
		@RequestParam(value = "kw", defaultValue = "") String kw) {
		Page<Group> paging = groupService.getList(kw, page);

		if (paging == null) {
			return "redirect:/main";
		}

		model.addAttribute("paging", paging);
		model.addAttribute("kw", kw);

		return "article/group/list";
	}

	@AllArgsConstructor
	@Getter
	public static class LikeResponse {
		private boolean success;
		private long newLikeCount;
	}

	@PostMapping("/like")
	public @ResponseBody RsData<ShareController.LikeResponse> like(Long id) {
		boolean success = groupService.like(rq.getAccount(), id);

		Group group = groupService.getGroup(id);

		if (group == null) {
			return RsData.of("F-1", "Fail");
		}

		long newLikeCount = group.getLikes();

		return RsData.of("S-1", "Success", new ShareController.LikeResponse(success, newLikeCount));
	}

	@PostMapping("/bookmark")
	public @ResponseBody boolean bookmark(Long id) {

		return groupService.bookmark(rq.getAccount(), id);
	}
}