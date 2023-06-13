package site.soloforest.soloforest.boundedContext.article.bookmark.controller;

import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.boundedContext.article.bookmark.service.BookmarkService;
import site.soloforest.soloforest.boundedContext.article.entity.Article;

@Controller
@RequiredArgsConstructor
public class BookmarkController {
	private final Rq rq;
	private final BookmarkService bookmarkService;

	@GetMapping("/bookmark")
	public String bookmark(Model model) {
		List<Article> articleList = bookmarkService.getList(rq.getAccount());

		model.addAttribute("articleList", articleList);

		return "article/bookmark";
	}
}
