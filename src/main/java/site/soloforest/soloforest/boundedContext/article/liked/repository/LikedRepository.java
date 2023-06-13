package site.soloforest.soloforest.boundedContext.article.liked.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.liked.entity.Liked;

public interface LikedRepository extends JpaRepository<Liked, Long> {
	Optional<Liked> findByArticleAndAccount(Article article, Account account);

	void deleteByArticleAndAccount(Article article, Account account);

	List<Liked> findAllByArticle(Article article);
}
