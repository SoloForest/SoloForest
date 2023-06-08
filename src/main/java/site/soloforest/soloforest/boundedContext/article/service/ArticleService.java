package site.soloforest.soloforest.boundedContext.article.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.repository.ArticleRepository;

@Service
@RequiredArgsConstructor
public class ArticleService {
	private final ArticleRepository articleRepository;

	public Article getArticle(Long id) {
		return articleRepository.findById(id).get();
	}
}
