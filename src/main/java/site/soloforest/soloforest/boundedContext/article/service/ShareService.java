package site.soloforest.soloforest.boundedContext.article.service;

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

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.base.rsData.RsData;
import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.repository.ShareRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {
	private final ShareRepository shareRepository;

	@Transactional
	public RsData<Share> create(String type, Account account, String subject, String content) {
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

		return RsData.of("S-1", "게시글이 등록되었습니다.", share);
	}

	public Page<Share> getList(String type, String kw, int page) {
		int boardNumber;
		if ("community".equals(type))
			boardNumber = 0;
		else if ("program".equals(type))
			boardNumber = 1;
		else
			return null;

		List<Sort.Order> sorts = new ArrayList<>();
		sorts.add(Sort.Order.desc("createDate"));

		Pageable pageable = PageRequest.of(page, 10, Sort.by(sorts));

		return shareRepository.findByBoardNumberAndKeyword(boardNumber, kw, pageable);
	}

	public Optional<Share> findById(Long id) {
		return shareRepository.findById(id);
	}

	@Transactional
	public void modifyViewd(Share share) {
		share.updateViewd();
		shareRepository.save(share);
	}

	public RsData<Share> canModify(Account account, Share share) {
		if (!Objects.equals(account.getId(), share.getAccount().getId())) {
			return RsData.of("F-1", "해당 게시글을 수정할 권한이 없습니다.");
		}

		return RsData.of("S-1", "게시글 수정이 가능합니다.");
	}

	@Transactional
	public RsData<Share> modify(Account account, Long id, String subject, String content) {
		Optional<Share> shareOptional = findById(id);

		if (shareOptional.isEmpty()) {
			return RsData.of("F-1", "존재하지 않는 게시글입니다.");
		}

		Share share = shareOptional.get();

		return modify(account, share, subject, content);
	}

	@Transactional
	public RsData<Share> modify(Account account, Share share, String subject, String content) {
		RsData<Share> canModifyRsData = canModify(account, share);

		if (canModifyRsData.isFail())
			return canModifyRsData;

		Share modifyShare = share.toBuilder()
			.subject(subject)
			.content(content)
			.modifyDate(LocalDateTime.now())
			.build();

		shareRepository.save(modifyShare);

		return RsData.of("S-1", "게시글을 수정하였습니다.", modifyShare);
	}

	@Transactional
	public Share delete(Long id) {
		Optional<Share> share = findById(id);

		if (share.isEmpty())
			return null;

		shareRepository.delete(share.get());
		return share.get();
	}

}
