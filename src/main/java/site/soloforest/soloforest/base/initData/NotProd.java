package site.soloforest.soloforest.base.initData;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.boundedContext.account.entity.Account;
import site.soloforest.soloforest.boundedContext.account.service.AccountService;
import site.soloforest.soloforest.boundedContext.article.entity.Group;
import site.soloforest.soloforest.boundedContext.article.entity.Share;
import site.soloforest.soloforest.boundedContext.article.repository.GroupRepository;
import site.soloforest.soloforest.boundedContext.article.repository.ShareRepository;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
	@Bean
	CommandLineRunner initData(
		AccountService accountService,
		ShareRepository shareRepository,
		GroupRepository groupRepository,
		CommentRepository commentRepository
	) {
		return new CommandLineRunner() {
			@Override
			@Transactional
			public void run(String... args) throws Exception {
				// TODO : 각 service의 create 함수가 merge되면 service를 사용해서 데이터를 생성하도록 넣어줍시다!
				Account accountAdmin1 = Account.builder()
					.username("admin").password("admin").nickname("관리자").email("admin@admin.admin")
					.authority(0)
					.build();
				List<Account> accounts = List.of(
					Account.builder()
						.username("usertest").password("test1").nickname("for test").email("test@test.com")
						.build(),
					Account.builder()
						.username("usertest2").password("test2").nickname("건조증").email("test2@test2.com")
						.address("강원도 춘천시")
						.build(),
					Account.builder()
						.username("usertest3").password("test3").nickname("여섯시").email("test3@test3.com")
						.address("서울특별시 용산구")
						.build()
				);
				accountService.create(accountAdmin1);
				accounts.forEach(accountService::create);

				List<Share> shares = List.of(
					Share.builder()
						.account(accountAdmin1)
						.subject("안녕하세요. SoloForest 입니다.")
						.content("즐거운 커뮤니티 환경 함께 만들어보아요.")
						.boardNumber(0)
						//.tag(1)
						.build(),
					Share.builder()
						.account(accounts.get(2))
						.subject("[서울 광진구] 취준백서")
						.content("광진구에서 취업 준비 중인 천년 1인가구를 위한 프로그램이 열린다고 합니다! 기간은 2023-05-26 ~ 2023-06-09라고 합니다.")
						.boardNumber(1)
						.imageUrl("/Logo_white.png")
						//.tag(2)
						.build()
				);
				shareRepository.saveAll(shares);

				List<Group> groups = List.of(
					Group.builder()
						.account(accounts.get(1)).subject("번개").content("용산에서 번개모임 가시죠!!").boardNumber(3)
						.member(4).money(3000).location("서울시 용산구")
						.startDate(LocalDateTime.of(2023, 6, 3, 12, 30))
						.endDate(LocalDateTime.of(2023, 6, 3, 19, 0))
						.build(),
					Group.builder()
						.account(accounts.get(2))
						.subject("쿠팡 스팸 공동구매")
						.content("쿠팡에서 스팸 세트 할인하는데 같이 나눠사실 분 계신가요? 신림 삽니다.")
						.boardNumber(2)
						.member(2).money(60000).location("서울시 관악구")
						.startDate(LocalDateTime.of(2023, 6, 1, 12, 00))
						.endDate(LocalDateTime.of(2023, 6, 10, 12, 00))
						.build()
				);
				//groupRepository.saveAll(groups);
				groupRepository.save(groups.get(0));
				groupRepository.save(groups.get(1));
				List<Comment> comments = List.of(
					Comment.builder()
						.writer(accounts.get(0)).article(shares.get(0)).content("넹")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accounts.get(1)).article(shares.get(0)).content("넵")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accounts.get(2)).article(shares.get(0)).content("누가 짱나게 하면 어떻게 하죠?")
						.secret(true)
						.build(),

					Comment.builder()
						.writer(accounts.get(0)).article(shares.get(1)).content("좋은 정보 감사합니다.")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accounts.get(2)).article(shares.get(1)).content("얼른 신청하세요!")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accountAdmin1).article(shares.get(1)).content("활발한 교류 부탁드립니다!")
						.secret(false)
						.build(),

					Comment.builder()
						.writer(accounts.get(2)).article(groups.get(0)).content("만나서 뭐하나요?")
						.secret(true)
						.build(),
					Comment.builder()
						.writer(accounts.get(1)).article(groups.get(0)).content("식사 후에 게임하러 가실분")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accounts.get(0)).article(groups.get(0)).content("저요!")
						.secret(false)
						.build(),

					Comment.builder()
						.writer(accounts.get(1)).article(groups.get(1)).content("인당 6만원인가요?")
						.secret(false)
						.build(),
					Comment.builder()
						.writer(accounts.get(0)).article(groups.get(1)).content("둘이 나눠도 비싸네여")
						.secret(false)
						.build()
				);
				commentRepository.saveAll(comments);

				List<Comment> replyComments = List.of(
					Comment.builder()
						.writer(accountAdmin1).article(shares.get(0)).content("신고하세요.")
						.secret(true).parent(comments.get(2))
						.build(),
					Comment.builder()
						.writer(accounts.get(2)).article(shares.get(0)).content("네 맞습니다!")
						.secret(false).parent(comments.get(9))
						.build()
				);
				commentRepository.saveAll(replyComments);
			}
		};
	}
}
