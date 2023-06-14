package site.soloforest.soloforest.boundedContext.notification.controller;

import static org.assertj.core.api.Assertions.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithUserDetails;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.transaction.annotation.Transactional;

import site.soloforest.soloforest.base.rq.Rq;
import site.soloforest.soloforest.boundedContext.account.controller.AccountController;
import site.soloforest.soloforest.boundedContext.account.repository.AccountRepository;
import site.soloforest.soloforest.boundedContext.notification.entity.Notification;
import site.soloforest.soloforest.boundedContext.notification.service.NotificationService;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
public class NotificationControllerTests {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private NotificationService notificationService;

	@Autowired
	private Rq rq;

	@Test
	@DisplayName("신고하기 테스트 - 신고하면 알림 객체 생성")
	@WithUserDetails("usertest2")
	void t001() throws Exception {
		ResultActions resultActions = mvc
			.perform(post("/account/report/2")
				.with(csrf())
			)
			.andDo(print());

		resultActions
			.andExpect(handler().handlerType(AccountController.class))
			.andExpect(handler().methodName("report"))
			.andExpect(status().isOk());

		assertThat(accountRepository.findById(2L).get().getReported()).isEqualTo(1);

		Notification notification = notificationService.getNotifications(accountRepository.findById(2L).get()).get(3);
		// NotProd account_id=2인 사용자의 기존 알림 객체 3개 -> 4번째 객체 시작 "누군가~" 라면 생성 성공
		assertThat(notification.getContent().startsWith("누군가"));
	}
}
