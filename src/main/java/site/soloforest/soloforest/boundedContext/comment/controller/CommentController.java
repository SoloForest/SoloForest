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

	// TODO : Rq 도입시 변경

	private final Rq rq;
	private final CommentService commentService;

	// 디버깅시 활용
	// private static final Logger logger = LoggerFactory.getLogger(CommentController.class);
	// 	logger.info("showComment 메서드 호출");

	@PreAuthorize("isAuthenticated()")
	@PostMapping("/create")
	public String create(Model model, @ModelAttribute CommentDTO commentDTO,
		@RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		String content = commentDTO.getCommentContents().trim();

		if (content.isBlank()) {
			return rq.redirectWithMsg("redirect:/article/share/detail/" + article.getId(), "내용을 입력해주세요");
		}
		// 부모댓글 생성
		Comment comment = commentService.create(content, commentDTO.getSecret(), account, article);

		model.addAttribute("article", article);

		Page<Comment> paging = commentService.getCommentPage(page, article);
		model.addAttribute("paging", paging);

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
		// ToDo : RsData 도입 고려
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

		// ToDo : RsData 고민
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

	// 댓글 삭제 메서드
	@PreAuthorize("isAuthenticated()")
	@PostMapping("/delete")
	public String delete(Model model, CommentDTO commentDTO, @RequestParam(defaultValue = "0") int page) {
		Article article = rq.getArticle(commentDTO.getArticleId());
		Account account = rq.getAccount();

		// ToDo : RsData 고려
		// 관리자가 아니거나 현재 로그인한 사용자가 작성한 댓글이 아니면 삭제 불가
		if (!(account.isAdmin()) && (account.getId() != commentDTO.getCommentWriter())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "삭제 권한이 없습니다");
		}

		Comment comment = commentService.getComment(commentDTO.getId());
		// 부모(댓글)이 있을 경우 연관관계 끊어주기 -> 삭제되더라도 GET 등으로 새로 요청을 보내는 것이 아니기에
		// 이 작업은 꼭 해줘야 대댓글 리스트도 수정된다!

		// 부모댓글이 삭제 되지 않았다면 연관관계 끊어주기만 하면 됨
		// => Ajax 비동기 리스트화를 위해 리스트에서 명시적 삭제
		if (comment.getParent() != null && !comment.getParent().isDeleted()) {
			comment.getParent().getChildren().remove(comment);
		}
		// 부모댓글이 삭제 상태이고 부모의 자식 댓글이 본인 포함 2개 이상이라면
		// 자식 댓글의 삭제가 부모 댓글 객체 삭제에 영향을 주지 않으니 연관관계만 끊어주기
		// => Ajax 비동기 리스트화를 위해 리스트에서 명시적 삭제
		else if (comment.getParent() != null && comment.getParent().isDeleted()
			&& comment.getParent().getChildren().size() > 1) {
			comment.getParent().getChildren().remove(comment);
		}

		commentService.delete(comment);

		model.addAttribute("article", article);
		Page<Comment> paging = commentService.getCommentPage(page, article);
		model.addAttribute("paging", paging);
		return "comment/comment :: #comment-list";
	}

}
