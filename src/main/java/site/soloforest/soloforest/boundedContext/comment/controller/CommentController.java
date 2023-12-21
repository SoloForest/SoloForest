package site.soloforest.soloforest.boundedContext.comment.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.server.ResponseStatusException;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.comment.dto.CommentDTO;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Controller
@RequiredArgsConstructor
@RequestMapping("/comment")
public class CommentController {
	private final Rq rq;
	private final CommentService commentService;

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(Model model, @ModelAttribute CommentDTO commentDTO,
		@RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		String content = commentDTO.getCommentContents();

		if (content == null || content.trim().isBlank()) {
			return rq.redirectWithMsg("redirect:/article/share/detail/" + article.getId(), "내용을 입력해주세요");
		}

		content = content.trim();

		// 부모댓글 생성
		Comment comment = commentService.create(content, commentDTO.getSecret(), account, article);
		model.addAttribute("article", article);

		int lastPage = commentService.getLastPageNumber(article);

		if (article.getBoardNumber() < 2) {
			return "redirect:/article/share/detail/" + article.getId() + "?page=" + lastPage + "#" + "comment_"
				+ comment.getId();
		}

		return "redirect:/article/group/detail/" + article.getId() + "?page=" + lastPage + "#" + "comment_"
			+ comment.getId();
	}

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/reply/create")
	public String replyCreate(Model model, @ModelAttribute CommentDTO commentDTO,
		@RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		// 부모 댓글 찾아오기
		Comment parent = commentService.getComment(commentDTO.getParentId());

		if (parent == null) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "해당 댓글을 찾을 수 없습니다");
		}
		// 자식 댓글 생성
		commentService.createReplyComment(commentDTO.getCommentContents(), commentDTO.getSecret(), account, article,
			parent);

		model.addAttribute("article", article);

		Page<Comment> paging = commentService.getCommentPage(page, article);
		model.addAttribute("paging", paging);

		return "comment/comment :: #comment-list";
	}

	// 답글 수정 + 댓글 수정 둘다
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/modify")
	public String modify(CommentDTO commentDTO, Model model, @RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		if (!(account.isAdmin()) && (account.getId() != commentDTO.getCommentWriter())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "수정권한이 없습니다");
		}

		// 대댓글(답글)도 id로 찾을 수 있음(댓글과 동일 객체 사용이 가능하니 하나의 메서드로 처리 가능)
		Comment comment = commentService.getComment(commentDTO.getId());

		// 댓글 내용, 비밀 댓글 여부만 수정 할테니 해당 값 넘기기
		commentService.modify(comment, commentDTO.getCommentContents().trim(), commentDTO.getSecret());

		model.addAttribute("article", article);
		Page<Comment> paging = commentService.getCommentPage(page, article);
		model.addAttribute("paging", paging);

		return "comment/comment :: #comment-list";
	}

	/*
	댓글 삭제 메서드
	   - 하나의 HTTP 요청 내에서 삭제, 조회할 경우 영속성 컨텍스트는 요청이 시작되고 끝까지 유지
	   - 따라서 요청 처리 중 삭제, 조회해도 변경된 DB 상태를 반영하지 않고 기존 캐싱 데이터 반환 가능
	   - 영속성 컨텍스트를 명시적으로 갱신하거나 연관관계를 끊어주는 방법 중 하나를 해야하는데
	     연관관계를 끊어주는 방식으로 구현한 메서드
	 */
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete")
	public String delete(Model model, CommentDTO commentDTO, @RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		// 관리자가 아니거나 현재 로그인한 사용자가 작성한 댓글이 아니면 삭제 불가
		if (!(account.isAdmin()) && (account.getId() != commentDTO.getCommentWriter())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
		}

		Comment comment = commentService.getComment(commentDTO.getId());
		Comment parent = comment.getParent();
		// 부모 댓글이 있고 삭제상태가 아니라면 단순히 연관 끊어주기
		// 영속성 컨텍스트가 하나의 요청에서 유지되기 때문에 이 작업은 필수로 해야한다.
		if (parent != null && !parent.isDeleted()) {
			comment.getParent().getChildren().remove(comment);
		}
		// 부모댓글이 삭제 상태이고 부모의 자식 댓글이 본인 포함 2개 이상이라면
		// 자식 댓글의 삭제가 부모 댓글 객체 삭제에 영향을 주지 않으니 연관관계만 끊어주기
		else if (parent != null && parent.isDeleted()
			// size를 3부터 하는 이유는, 여기서 remove를 해줘도 comment객체 자체는 남기에,
			// size가 2라면, 서비스로 넘어갈때는 1로 줄어든 상태로 넘어간다.
			// 그러면 서비스에서는 자식이 1개니까 지워버리면 될 것이라 판단하기에 size가 3부터 처리해야 함
			// 어차피 서비스 내부에서 remove(comment) 해도 이미 삭제되어 오류가 뜨지 않으니까 그냥 무시되니 괜찮음
			&& parent.getChildren().size() > 2) {
			comment.getParent().getChildren().remove(comment);
		}

		// JPA delete 메서드는 영속성 컨텍스트에서 바로 삭제되기에 이후 article로 댓글 조회해도 나타나지 않음
		commentService.delete(comment);

		model.addAttribute("article", article);
		// 영속성 컨텍스트는 하나의 요청에 유지되기 때문에, 삭제하고 다시 조회해도 이전에 캐싱된 값 활용
		// 따라서 위에서 리스트 명시적으로 연관을 끊어주고 다시 댓글을 조회한 것임
		Page<Comment> paging = commentService.getCommentPage(page, article);
		model.addAttribute("paging", paging);
		return "comment/comment :: #comment-list";
	}
}
