package site.soloforest.soloforest.boundedContext.article.bookmark.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.bookmark.entity.Bookmark;
import site.soloforest.soloforest.boundedContext.article.entity.Article;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {
	Optional<Bookmark> findByArticleAndAccount(Article article, Account account);

	void deleteByArticleAndAccount(Article article, Account account);
}
