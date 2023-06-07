package site.soloforest.soloforest.boundedContext.comment.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.event.EventCommentCreate;
import site.soloforest.soloforest.base.event.EventReplyCommentCreate;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.controller.CommentController;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CommentService {

	private final CommentRepository commentRepository;

	private final ApplicationEventPublisher publisher;

	@Transactional
	public Comment create(String content, boolean secret, Account account, Article article) {
		Comment newComment = Comment.builder()
			.content(content)
			.writer(account)
			.article(article)
			.secret(secret)
			.build();

		// 댓글 생성
		commentRepository.save(newComment);

		// 알림 객체 생성 이벤트 발생
		publisher.publishEvent(new EventCommentCreate(this, newComment));
		return newComment;
	}

	@Transactional
	public Comment createReplyComment(String content, boolean secret, Account account, Article article, Comment parentComment) {
		Comment newComment = Comment.builder()
			.content(content)
			.writer(account)
			.article(article)
			.secret(secret)
			.parent(parentComment)
			.build();

		// 대댓글 저장
		commentRepository.save(newComment);

		// 대댓글 작성 알림 객체 생성
		publisher.publishEvent(new EventReplyCommentCreate(this, newComment));

		return newComment;
	}


	// 테스트용
	public Comment getComment(Long commentId) {
		return commentRepository.findById(commentId).orElse(null);
	}

	// 댓글 수정
	@Transactional
	public Comment modify(Comment comment, String content, boolean secret) {
		Comment mComment = comment.toBuilder()
			.content(content)
			.secret(secret)
			.build();
		commentRepository.save(mComment);

		return mComment;
	}

	// 삭제
	@Transactional
	public void delete(Comment comment) {
		if (comment.getChildren().size() != 0) {
			// 자식이 있으면 내용만 삭제
			comment.deleteParent();
		}
		else { // 자식이 없다 -> 대댓글이 없다 -> 객체 그냥 삭제해도 된다.
			// 삭제 가능한 조상 댓글을 구해서 삭제
			// ex) 할아버지 - 아버지 - 대댓글, 3자라 했을 때 대댓글 입장에서 자식이 없으니 삭제 가능
			// => 삭제하면 아버지도 삭제 가능 => 할아버지도 삭제 가능하니 이런식으로 조상 찾기 메서드
			commentRepository.delete(getDeletableAncestorComment(comment));
		}
	}

	private Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
		if (parent != null && parent.getChildren().size() == 1 && parent.getContent().equals("삭제되었습니다."))
			// 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
			// 삭제가능 댓글 -> 만일 댓글의 조상(대댓글의 입장에서 할아버지 댓글)도 해당 댓글 삭제 시 삭제 가능한지 확인
			return getDeletableAncestorComment(parent);
		// 삭제 -> Cascade 옵션으로 가장 부모만 삭제 해도 자식들도 다 삭제 가능
		return comment;
	}

	// 댓글 조회
	public Comment findById(Long id) {
			return commentRepository.findById(id).orElse(null);
	}
}
