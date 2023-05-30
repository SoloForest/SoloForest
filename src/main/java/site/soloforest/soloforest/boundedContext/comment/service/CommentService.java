package site.soloforest.soloforest.boundedContext.comment.service;

import java.security.Principal;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.comment.controller.CommentController;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;

	@Transactional
	public Comment create(String content) {
		Comment newComment = Comment.builder()
			.content(content)
			.build();

		commentRepository.save(newComment);

		return newComment;
	}

	public Comment getComment(String content) {
		 return commentRepository.findByContent(content);
	}

}
