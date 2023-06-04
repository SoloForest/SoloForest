package site.soloforest.soloforest.boundedContext.article.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.repository.ShareRepository;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShareService {
	private final ShareRepository shareRepository;

	@Transactional
	public Share create(int boardNumber, String subject, String content) {
		Share s = Share
			.builder()
			.subject(subject)
			.content(content)
			.boardNumber(boardNumber)
			.build();

		shareRepository.save(s);

		return s;
	}

	public List<Share> getSharesByBoardNumber(int boardNumber) {
		return shareRepository.findByBoardNumber(boardNumber);
	}

	public Optional<Share> getShare(Long id) {
		return shareRepository.findById(id);
	}
}
