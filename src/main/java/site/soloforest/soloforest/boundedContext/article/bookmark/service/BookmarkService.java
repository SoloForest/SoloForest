package site.soloforest.soloforest.boundedContext.article.bookmark.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.bookmark.entity.Bookmark;
import site.soloforest.soloforest.boundedContext.article.bookmark.repository.BookmarkRepository;
import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.repository.GroupRepository;
import site.soloforest.soloforest.boundedContext.article.repository.ShareRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookmarkService {
	private final BookmarkRepository bookmarkRepository;
	private final ShareRepository shareRepository;
	private final GroupRepository groupRepository;

	public Page<Article> getList(Account account, int page) {
		List<Bookmark> bookmarkList = bookmarkRepository.findAllByAccount(account);

		List<Article> articleList = new ArrayList<>();
		for (Bookmark bookmark : bookmarkList) {
			Article article = bookmark.getArticle();
			int boardNumber = article.getBoardNumber();

			if (boardNumber == 0 || boardNumber == 1) {
				Optional<Share> articleOptional = shareRepository.findById(article.getId());
				articleOptional.ifPresent(articleList::add);
			} else {
				Optional<Group> articleOptional = groupRepository.findById(article.getId());
				articleOptional.ifPresent(articleList::add);
			}
		}

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));

		//페이징 처리
		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));
		int start = Math.toIntExact(pageable.getOffset());
		int end = Math.min((start + pageable.getPageSize()), articleList.size());
		articleList.sort((a1, a2) -> a2.getCreateDate().compareTo(a1.getCreateDate()));

		List<Article> pageableList = articleList.subList(start, end);

		return new PageImpl<>(pageableList, pageable, articleList.size());
	}
}
