package site.soloforest.soloforest.boundedContext.article.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.repository.GroupRepository;

@Service
@RequiredArgsConstructor
public class GroupService {

	private final GroupRepository groupRepository;

	public Group getGroup(Long id) {
		return groupRepository.findById(id).orElse(null);
	}
}
