package site.soloforest.soloforest.boundedContext.home.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.service.ArticleService;

@Controller
@RequiredArgsConstructor
public class HomeController {
	private final ArticleService articleService;

	@GetMapping("/")
	public String root() {
		return "redirect:/main";
	}

	@GetMapping("/main")
	public String showMain(Model model) {
		List<Article> communityList = articleService.findByBoardNumber(0);
		List<Article> programList = articleService.findByBoardNumber(1);
		List<Article> groupList = articleService.findByBoardNumber(2);

		model.addAttribute("communityList", communityList);
		model.addAttribute("programList", programList);
		model.addAttribute("groupList", groupList);

		return "home/main";
	}
}
