package site.soloforest.soloforest.boundedContext.article.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.service.GroupService;

@Controller
@RequestMapping("/article/group")
@RequiredArgsConstructor
public class GroupController {
	private final GroupService groupService;

	@GetMapping("detail/{id}")
	public String test(Model model, @PathVariable("id") Long id) {

		Group group = groupService.getGroup(id);
		model.addAttribute("GroupData", group);

		return "article/group";
	}

}
