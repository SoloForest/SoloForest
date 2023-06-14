package site.soloforest.soloforest.boundedContext.article.service;

import java.util.List;

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

	public List<Article> findByBoardNumber(int boardNumber) {
		return articleRepository.findAllByBoardNumber(boardNumber);
	}

	public List<Article> findTop3LatestByBoardNumber(int boardNumber) {
		return articleRepository.findTop3ByBoardNumberOrderByCreateDateDesc(boardNumber);
	}

	public List<Article> findTop5LatestByBoardNumber(int boardNumber) {
		return articleRepository.findTop5ByBoardNumberOrderByCreateDateDesc(boardNumber);
	}
}
