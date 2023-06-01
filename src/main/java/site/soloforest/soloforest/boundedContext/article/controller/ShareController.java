package site.soloforest.soloforest.boundedContext.article.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.RequiredArgsConstructor;

@Controller
@RequestMapping("/article/share")
@RequiredArgsConstructor
public class ShareController {
	@GetMapping("")
	public String showList() {
		return "article/share/list";
	}
}
