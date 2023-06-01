package site.soloforest.soloforest.boundedContext.comment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import site.soloforest.soloforest.boundedContext.comment.entity.Comment;


public interface CommentRepository extends JpaRepository<Comment, Long> {
	Comment findByContent(String content);
}
