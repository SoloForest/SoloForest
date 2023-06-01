package site.soloforest.soloforest.boundedContext.article.service;

import java.util.List;

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
	public void create(String subject, String content) {
		Share s = Share
			.builder()
			.subject(subject)
			.content(content)
			.build();
		shareRepository.save(s);
	}

	public List<Share> getShare() {
		return shareRepository.findAll();
	}
}
