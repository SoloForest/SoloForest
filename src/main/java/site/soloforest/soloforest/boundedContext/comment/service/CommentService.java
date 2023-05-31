package site.soloforest.soloforest.boundedContext.comment.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;

	// 작성자 정보를 위해 로그인한 회원 정보 받아서 넣기는 아직 안함
	@Transactional
	public Comment create(String content) {
		Comment newComment = Comment.builder()
			.content(content)
			.build();

		commentRepository.save(newComment);

		return newComment;
	}

	// 테스트용
	public Comment getComment(String content) {
		 return commentRepository.findByContent(content);
	}

	public Comment getComment(Long commentId) {
		return commentRepository.findById(commentId).get();
	}

	// 수정, 아직 대댓글, 비밀댓글 고려 안함
	@Transactional
	public Comment modify(Comment comment, String content) {
		Comment mComment = comment.toBuilder()
			.content(content)
			.build();
		commentRepository.save(mComment);

		return mComment;
	}

	// 삭제
	@Transactional
	public void delete(Comment comment) {
		commentRepository.delete(comment);
	}
}
