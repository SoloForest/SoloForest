package site.soloforest.soloforest.boundedContext.comment.service;

import java.security.Principal;

import org.springframework.stereotype.Service;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.comment.controller.CommentController;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
public class CommentService {

	private final CommentRepository commentRepository;

	public Comment create(String content) {
		Comment newComment = Comment.builder()
			.content(content)
//			.article(article)
//			.writer(account)
			.build();

		commentRepository.save(newComment);

		return newComment;
	}

	public Comment getComment(String content) {
		 return commentRepository.findByContent(content);
	}

}
