package site.soloforest.soloforest.boundedContext.article.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
	List<Article> findAllByBoardNumber(int boardNumber);
}

