package site.soloforest.soloforest.boundedContext.comment.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.event.EventCommentCreate;
import site.soloforest.soloforest.base.event.EventReplyCommentCreate;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
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
	public Comment createReplyComment(String content, boolean secret, Account account, Article article,
		Comment parentComment) {
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
			.modifyDate(LocalDateTime.now())
			.build();
		commentRepository.save(mComment);

		return mComment;
	}

	// 삭제
	@Transactional
	public void delete(Comment comment) {
		if (comment == null) {
			throw new IllegalArgumentException("Comment cannot be null");
		}
		if (comment.getChildren().size() != 0) {
			// 자식이 있으면 삭제 상태만 변경
			comment.deleteParent();
		} else { // 자식이 없다 -> 대댓글이 없다 -> 객체 그냥 삭제해도 된다.
			// 삭제 가능한 조상 댓글을 구해서 삭제
			// 조상 댓글을 구하는 이유는 만일 자식(본인)이 있기 때문에 삭제가 안된 조상을 찾음
			// ex) 할아버지(삭제상태) - 아버지(삭제상태) - 대댓글, 3자라 했을 때 대댓글을 지우면 모두 삭제됨
			Comment tmp = getDeletableAncestorComment(comment);
			commentRepository.delete(tmp);
		}
	}

	/*
		삭제 가능한 최대 조상 찾기 메서드
		본인 때문에 삭제가 안되던 가장 최상위 조상을 삭제시킨다.
	 */
	@Transactional
	public Comment getDeletableAncestorComment(Comment comment) {
		Comment parent = comment.getParent(); // 현재 댓글의 부모를 구함
		if (parent != null && parent.getChildren().size() == 1 && parent.isDeleted() == true) {
			// 부모가 있고, 부모의 자식이 1개(지금 삭제하는 댓글)이고, 부모의 삭제 상태가 TRUE인 댓글이라면 재귀
			// 삭제가능 댓글 -> 만일 댓글의 조상(대댓글의 입장에서 할아버지 댓글)도 해당 댓글 삭제 시 삭제 가능한지 확인
			// 삭제 -> 부모와 연관 끊어주고 삭제 대상으로 넘겨줘서 delete하기에 계속 삭제됨

			// 대댓글 1개인거 삭제할 때 연관관계 삭제하고 부모 댓글 삭제하기 필요
			// 컨트롤러가 아닌 서비스의 삭제에서 처리해주는 이유는 연관관계를 삭제해주면 parent를 구할 수 없기에 여기서 끊어줘야 함
			// 연관관계만 끊어주면 orphanRemoval 옵션으로 자식 객체는 삭제되니 부모를 삭제 대상으로 넘기면 이후 자동 삭제
			parent.getChildren().remove(comment);

			return getDeletableAncestorComment(parent);
		}

		// 대댓글이 2개일때 -> 컨트롤러에서 remove를 하고 서비스로 넘어오면, 사이즈가 또 1이되서 위에 if문의 조건을 만족
		// 그러면 부모 댓글을 삭제해도 된다 생각하고 위의 if문에서 부모 댓글을 반환해 부모 댓글을 지운다.
		// 그렇게 되면 실제 자식 객체는 고아객체가 되어 사라진다.
		// 그걸 방지하고자 사이즈가 2라면 컨틀롤러에서 자식 지우고 오지 않고, 이 메서드에서 연관관계만 끊고 삭제하라 명령한다.
		if (parent != null && parent.getChildren().size() == 2 && parent.isDeleted() == true) {
			parent.getChildren().remove(comment);
			return comment;
		}

		return comment;
	}

	// 댓글 조회
	public Comment findById(Long id) {
		return commentRepository.findById(id).orElse(null);
	}

	public List<Comment> getCommentList(Article article) {
		List<Comment> commentList = commentRepository.findAllByArticle(article);
		return commentList;
	}

	// 페이지화
	public Page<Comment> getCommentPage(int page, Article article) {
		Pageable pageable = PageRequest.of(page, 10); // 페이지네이션 정보
		return commentRepository.findAllByArticle(article, pageable);
	}

	// 마지막 페이지 번호 가져오기
	public int getLastPageNumber(Article article) {
		int commentCount = commentRepository.countByArticle(article);
		int pageSize = 10; // 페이지 당 댓글 수 (조정 가능)
		int lastPageNumber = (int)Math.ceil((double)commentCount / pageSize);
		// 스프링 0페이지 부터 시작하니 1 빼주기
		// 단 댓글이 1개도 없던 상황일 경우, 댓글이 달린 직후에는 0일테니 -1하면 음수값이 나온다.
		// 따라서 음수이면 0을 반환하도록 수정
		if (lastPageNumber - 1 < 0)
			return 0;
		else
			return lastPageNumber - 1;
	}

	// 현재 페이지 번호 가져오기
	public int getPage(Comment comment) {
		int pageSize = 10; // 페이지 당 댓글 수 (조정 가능)
		// 인자로 받은 댓글보다 이전에 달린 댓글 갯수 / 페이지 당 갯수 => 현재 댓글이 있는 페이지 확인
		int commentIndex =
			commentRepository.countByArticleAndCreateDateBefore(comment.getArticle(), comment.getCreateDate());
		int pageNumber = (int)Math.ceil((double)commentIndex / pageSize);
		// 스프링 0페이지 부터 시작하니 1 빼주기
		// 단 댓글이 1개도 없던 상황일 경우, 댓글이 달린 직후에는 0일테니 -1하면 음수값이 나온다.
		// 따라서 음수이면 0을 반환하도록 수정
		if (pageNumber - 1 < 0)
			return 0;
		else
			return pageNumber - 1;
	}
}
