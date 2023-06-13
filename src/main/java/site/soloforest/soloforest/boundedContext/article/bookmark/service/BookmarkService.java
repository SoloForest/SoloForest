package site.soloforest.soloforest.boundedContext.article.bookmark.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

	public List<Article> getList(Account account) {
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

		return articleList;
	}
}
