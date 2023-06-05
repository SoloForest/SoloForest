package site.soloforest.soloforest.boundedContext.article.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.article.entity.Article;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
