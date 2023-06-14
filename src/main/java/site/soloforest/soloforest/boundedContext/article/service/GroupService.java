package site.soloforest.soloforest.boundedContext.article.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.bookmark.entity.Bookmark;
import site.soloforest.soloforest.boundedContext.article.bookmark.repository.BookmarkRepository;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.liked.entity.Liked;
import site.soloforest.soloforest.boundedContext.article.liked.repository.LikedRepository;
import site.soloforest.soloforest.boundedContext.article.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;
	private final LikedRepository likedRepository;
	private final BookmarkRepository bookmarkRepository;

	@Transactional
	public boolean like(Account account, Long id) {
		Optional<Group> groupOptional = groupRepository.findById(id);

		if (groupOptional.isEmpty())
			return false;

		Group group = groupOptional.get();

		if (alreadyLiked(group, account)) {
			likedRepository.deleteByArticleAndAccount(group, account);
			group.downLikes();

			return false;
		}

		Liked liked = Liked
			.builder()
			.article(group)
			.account(account)
			.build();

		likedRepository.save(liked);
		group.upLikes();

		return true;
	}

	public boolean alreadyBookmarked(Group group, Account account) {
		Optional<Bookmark> bookmarkOptional = bookmarkRepository.findByArticleAndAccount(group, account);

		return bookmarkOptional.isPresent();
	}

	@Transactional
	public boolean bookmark(Account account, Long id) {
		Optional<Group> groupOptional = groupRepository.findById(id);

		if (groupOptional.isEmpty()) {
			return false;
		}

		Group group = groupOptional.get();

		if (alreadyBookmarked(group, account)) {
			bookmarkRepository.deleteByArticleAndAccount(group, account);

			return false;
		}

		Bookmark bookmark = Bookmark
			.builder()
			.article(group)
			.account(account)
			.build();

		bookmarkRepository.save(bookmark);
		return true;
	}

	public boolean alreadyLiked(Group group, Account account) {
		Optional<Liked> likeOptional = likedRepository.findByArticleAndAccount(group, account);

		return likeOptional.isPresent();
	}

	public Group getGroup(Long id) {
		return groupRepository.findById(id).orElse(null);

	}

	@Transactional
	public void modifyViewed(Group group) {
		group.updateViewed();
		groupRepository.save(group);
	}

	@Transactional
	public Group create(String subject, String content, int member, LocalDateTime startDate, LocalDateTime endDate
		, int money, String location, Account account) {
		Group group = Group.builder()
			.account(account)
			.subject(subject)
			.content(content)
			.member(member)
			.startDate(startDate)
			.endDate(endDate)
			.money(money)
			.location(location)
			.boardNumber(2)
			.build();
		groupRepository.save(group);

		return group;

	}

	@Transactional
	public void modify(Group group, String subject, String content) {
		groupRepository.save(group.toBuilder().id(group.getId()).subject(subject).content(content).build());
	}

	public void delete(Group group) {
		List<Liked> likedList = likedRepository.findAllByArticle(group);
		likedRepository.deleteAll(likedList);
		List<Bookmark> bookmarkList = bookmarkRepository.findAllByArticle(group);
		bookmarkRepository.deleteAll(bookmarkList);

		groupRepository.delete(group);
	}

	public Page<Group> getList(String kw, int page) {

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

		return groupRepository.findGroupBykeyword(kw, pageable);
	}

}
