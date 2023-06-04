package site.soloforest.soloforest.boundedContext.article.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import site.soloforest.soloforest.boundedContext.article.entity.Article;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.repository.ArticleRepository;
import site.soloforest.soloforest.boundedContext.article.repository.GroupRepository;

@Service
public class GroupService {

	private final ArticleRepository articleRepository;
	private final GroupRepository groupRepository;

	public GroupService(ArticleRepository articleRepository,
		GroupRepository groupRepository) {
		this.articleRepository = articleRepository;
		this.groupRepository = groupRepository;
	}

	public Group getId(Long id) {
		Optional<Group> myEntity = groupRepository.findById(id);
		return myEntity.get();
	}

	public Article getArticle(Long id) {
		Optional<Article> optionalMyEntity = articleRepository.findById(id);
		return optionalMyEntity.get();
	}
}
