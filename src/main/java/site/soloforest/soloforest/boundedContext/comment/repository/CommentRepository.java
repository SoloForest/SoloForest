package site.soloforest.soloforest.boundedContext.comment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
	Comment findByContent(String content);

	List<Comment> findAllByArticleOrderByIdDesc(Article article);
}
