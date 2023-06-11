package site.soloforest.soloforest.boundedContext.article.service;

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

	public Page<Share> getSharesByBoardNumber(String type, String kw, int page) {
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

	public Optional<Share> getShare(Long id) {
		return shareRepository.findById(id);
	}

	@Transactional
	public void modifyViewd(Share share) {
		share.updateViewd();
		shareRepository.save(share);
	}

	@Transactional
	public void modify(Share share, String subject, String content) {
		Share modifyShare = share.toBuilder()
			.subject(subject)
			.content(content)
			.build();

		shareRepository.save(modifyShare);
	}

	@Transactional
	public Share delete(Long id) {
		Optional<Share> share = getShare(id);

		if (share.isEmpty())
			return null;

		shareRepository.delete(share.get());
		return share.get();
	}
}
