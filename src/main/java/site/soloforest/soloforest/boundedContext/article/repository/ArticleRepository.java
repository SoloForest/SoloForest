package site.soloforest.soloforest.boundedContext.article.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.soloforest.soloforest.boundedContext.article.entity.Article;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	Optional<Article> findById(Long id);
}