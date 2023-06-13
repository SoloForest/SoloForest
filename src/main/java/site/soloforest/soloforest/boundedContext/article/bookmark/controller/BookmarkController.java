package site.soloforest.soloforest.boundedContext.article.bookmark.controller;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
	public String bookmark(Model model, @RequestParam(value = "page", defaultValue = "0") int page) {
		Page<Article> paging = bookmarkService.getList(rq.getAccount(), page);

		model.addAttribute("paging", paging);

		return "article/bookmark";
	}
}
