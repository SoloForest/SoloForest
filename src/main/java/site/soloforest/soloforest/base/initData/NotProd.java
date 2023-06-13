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
import site.soloforest.soloforest.boundedContext.article.service.ShareService;
import site.soloforest.soloforest.boundedContext.comment.entity.Comment;
import site.soloforest.soloforest.boundedContext.comment.repository.CommentRepository;
import site.soloforest.soloforest.boundedContext.comment.service.CommentService;

@Configuration
@Profile({"dev", "test"})
public class NotProd {
	@Bean
	CommandLineRunner initData(
		AccountService accountService,
		ShareRepository shareRepository,
		ShareService shareService,
		GroupRepository groupRepository,
		CommentRepository commentRepository,
		CommentService commentService
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

				//커뮤니티 게시판 게시글 생성
				Share community1 = shareService.create("community", accountAdmin1, "안녕하세요. SoloForest 입니다.",
						"즐거운 커뮤니티 환경 함께 만들어보아요.", null)
					.getData();
				//프로그램 게시판 게시글 생성
				Share program1 = shareService.create("program", accounts.get(0), "[서울 광진구] 취준백서",
					"광진구에서 취업 준비 중인 천년 1인가구를 위한 프로그램이 열린다고 합니다! 기간은 2023-05-26 ~ 2023-06-09라고 합니다.",
					"/share/sample1.jpg").getData();
				Share program2 = shareService.create("program", accounts.get(1), "[서울 동대문구] 동일이의 득심득심",
					"힐링 명상을 통해 함께 마음 수련을 해보아요.", "/share/sample2.png").getData();
				Share program3 = shareService.create("program", accounts.get(2), "[서울 강북구] 1인가구 개인상담",
					"상담 전문가와 함께 1인 가구를 위한 상담을 진행한다고 하네요. 관심있으신 분은 신청하세요.", "/share/sample3.png").getData();

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

				// 댓글 생성
				Comment comment1 = commentService.create("넹", false, accounts.get(0), community1);
				Comment comment2 = commentService.create("넵", false, accounts.get(1), community1);
				Comment comment3 = commentService.create("누가 짱나게 하면 어떻게 하죠?", false, accounts.get(2), community1);
				Comment comment4 = commentService.create("좋은 정보 감사합니다.", false, accounts.get(0), program1);
				Comment comment5 = commentService.create("얼른 신청하세요!", false, accounts.get(2), program1);
				Comment comment6 = commentService.create("활발한 교류 부탁드립니다!", false, accountAdmin1, program1);
				Comment comment7 = commentService.create("만나서 뭐하나요?", false, accounts.get(2), groups.get(0));
				Comment comment8 = commentService.create("식사 후에 게임하러 가실분", false, accounts.get(1), groups.get(0));
				Comment comment9 = commentService.create("저요!", false, accounts.get(0), groups.get(0));
				Comment comment10 = commentService.create("인당 6만원인가요?", false, accounts.get(1), groups.get(0));
				Comment comment111 = commentService.create("둘이 나눠도 비싸네여", false, accounts.get(0), groups.get(1));

				Comment replyComment1 = commentService.createReplyComment("신고하세요", false, accountAdmin1, community1,
					comment3);
				Comment replyComment2 = commentService.createReplyComment("네 맞습니다!", false, accounts.get(2),
					community1, comment10);
				Comment replyComment3 = commentService.createReplyComment("가시죠!!", false, accounts.get(1),
					program1, comment8);

			}
		};
	}
}
