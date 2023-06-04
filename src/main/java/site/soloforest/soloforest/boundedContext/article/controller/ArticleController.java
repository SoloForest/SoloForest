package site.soloforest.soloforest.boundedContext.article.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.service.GroupService;

@Controller
@RequestMapping("/article")

public class ArticleController {

	private GroupService groupService;

	public ArticleController(GroupService groupService) {
		this.groupService = groupService;
	}

	@GetMapping("/group/{id}")
	public String Article(Model model, @PathVariable("id") Long id) {
		Article article = groupService.getArticle(id);
		model.addAttribute("ArticleData", article);

		Group group = groupService.getId(id);
		model.addAttribute("GroupData", group);

		return "article/group";
	}
}