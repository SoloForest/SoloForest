package site.soloforest.soloforest.boundedContext.article.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.bookmark.entity.Bookmark;
import site.soloforest.soloforest.boundedContext.article.bookmark.repository.BookmarkRepository;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.liked.entity.Liked;
import site.soloforest.soloforest.boundedContext.article.liked.repository.LikedRepository;
import site.soloforest.soloforest.boundedContext.article.repository.ShareRepository;
import site.soloforest.soloforest.boundedContext.picture.entity.Picture;
import site.soloforest.soloforest.boundedContext.picture.pictureHandler.PictureHandler;
import site.soloforest.soloforest.boundedContext.picture.repository.PictureRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {
	private final ShareRepository shareRepository;
	private final LikedRepository likedRepository;
	private final BookmarkRepository bookmarkRepository;
	private final PictureRepository pictureRepository;
	private final PictureHandler pictureHandler;

	@Transactional
	public RsData<Share> create(String type, Account account, String subject, String content,
		MultipartFile multipartFile) throws
		IOException {
		int boardNumber = -1;

		if ("community".equals(type))
			boardNumber = 0;
		else if ("program".equals(type))
			boardNumber = 1;
		else
			RsData.failOf(null);

		Share share = Share
			.builder()
			.account(account)
			.boardNumber(boardNumber)
			.subject(subject)
			.content(content)
			.build();

		shareRepository.save(share);

		//사진 업로드
		RsData<Picture> picture = pictureHandler.parseFileInfo(multipartFile);
		if (picture != null) {
			if (picture.isSuccess()) {
				Share pictureShare = share.toBuilder()
					.picture(picture.getData())
					.build();

				shareRepository.save(pictureShare);
			} else { //사진 업로드 실패할 경우 알림 출력
				String pictureResultCode = picture.getResultCode();
				String pictureMsg = picture.getMsg();
				return RsData.of(pictureResultCode, pictureMsg);
			}
		}
		return RsData.of("S-1", "게시글이 등록되었습니다.", share);
	}

	public Page<Share> getList(String type, String kw, int page) {
		int boardNumber;
		int pageSize;
		if ("community".equals(type)) {
			boardNumber = 0;
			pageSize = 10;
		} else if ("program".equals(type)) {
			boardNumber = 1;
			pageSize = 6;
		} else
			return null;

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));

		Pageable pageable = PageRequest.of(page, pageSize, Sort.by(sorts));

		return shareRepository.findByBoardNumberAndKeyword(boardNumber, kw, pageable);
	}

	public Optional<Share> findById(Long id) {
		return shareRepository.findById(id);
	}

	@Transactional
	public void modifyViewed(Share share) {
		share.updateViewed();
		shareRepository.save(share);
	}

	public RsData<Share> canModify(Account account, Share share) {
		if (!Objects.equals(account.getId(), share.getAccount().getId())) {
			return RsData.of("F-1", "해당 게시글을 수정할 권한이 없습니다.");
		}

		return RsData.of("S-1", "게시글 수정이 가능합니다.");
	}

	@Transactional
	public RsData<Share> modify(Account account, Long id, String subject, String content,
		MultipartFile multipartFile) throws
		IOException {
		Optional<Share> shareOptional = findById(id);

		if (shareOptional.isEmpty()) {
			return RsData.of("F-1", "존재하지 않는 게시글입니다.");
		}

		Share share = shareOptional.get();

		return modify(account, share, subject, content, multipartFile);
	}

	@Transactional
	public RsData<Share> modify(Account account, Share share, String subject, String content,
		MultipartFile multipartFile) throws IOException {
		RsData<Share> canModifyRsData = canModify(account, share);

		if (canModifyRsData.isFail())
			return canModifyRsData;

		Share modifyShare = share.toBuilder()
			.subject(subject)
			.content(content)
			.modifyDate(LocalDateTime.now())
			.build();

		//사진 업로드
		RsData<Picture> picture = pictureHandler.parseFileInfo(multipartFile);
		if (picture != null) {
			if (picture.isSuccess()) {
				if (share.getPicture() != null) {
					pictureRepository.delete(share.getPicture());
				}
				modifyShare = share.toBuilder()
					.picture(picture.getData())
					.build();

			} else { //사진 업로드 실패할 경우 알림 출력
				String pictureResultCode = picture.getResultCode();
				String pictureMsg = picture.getMsg();
				return RsData.of(pictureResultCode, pictureMsg);
			}
		}

		shareRepository.save(modifyShare);

		return RsData.of("S-1", "게시글을 수정하였습니다.", modifyShare);
	}

	@Transactional
	public RsData<Share> delete(Account account, Share share) {

		if (share == null)
			return RsData.of("F-1", "이미 삭제된 게시글 입니다.");

		// 관리자도 아니고 게시글 작성자도 아니면 거절
		if (!Objects.equals(account.getId(), share.getAccount().getId()) && !(account.isAdmin()))
			return RsData.of("F-2", "해당 게시글을 삭제할 권한이 없습니다.");

		//게시글 삭제 전, 게시글과 관련된 좋아요, 즐겨찾기, 사진 삭제
		List<Liked> likedList = likedRepository.findAllByArticle(share);
		likedRepository.deleteAll(likedList);
		List<Bookmark> bookmarkList = bookmarkRepository.findAllByArticle(share);
		bookmarkRepository.deleteAll(bookmarkList);
		if (share.getPicture() != null) {
			Optional<Picture> picture = pictureRepository.findById(share.getPicture().getId());
			picture.ifPresent(pictureRepository::delete);
		}
		shareRepository.delete(share);

		return RsData.of("S-1", "게시글이 삭제되었습니다.");
	}

	public boolean findLike(Share share, Account account) {
		long count = likedRepository.countByArticleAndAccount(share, account);

		return count > 0;
	}

	@Transactional
	public boolean like(Account account, Long shareId) {
		Optional<Share> shareOptional = shareRepository.findById(shareId);

		if (shareOptional.isEmpty())
			return false;

		Share share = shareOptional.get();

		if (findLike(share, account)) {
			likedRepository.deleteByArticleAndAccount(share, account);
			share.downLikes();

			return false;
		}

		Liked liked = Liked
			.builder()
			.article(share)
			.account(account)
			.build();

		likedRepository.save(liked);
		share.upLikes();

		return true;
	}

	public boolean findBookmark(Share share, Account account) {
		long count = bookmarkRepository.countByArticleAndAccount(share, account);

		return count > 0;
	}

	@Transactional
	public boolean bookmark(Account account, Long shareId) {
		Optional<Share> shareOptional = shareRepository.findById(shareId);

		if (shareOptional.isEmpty())
			return false;

		Share share = shareOptional.get();

		if (findBookmark(share, account)) {
			bookmarkRepository.deleteByArticleAndAccount(share, account);

			return false;
		}

		Bookmark bookmark = Bookmark
			.builder()
			.article(share)
			.account(account)
			.build();

		bookmarkRepository.save(bookmark);
		return true;
	}
}
