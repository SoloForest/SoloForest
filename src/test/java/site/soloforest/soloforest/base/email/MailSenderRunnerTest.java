package site.soloforest.soloforest.base.email;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

import site.soloforest.soloforest.SoloForestApplication;

@SpringBootTest
@RunWith(SpringRunner.class)
@ContextConfiguration(classes = SoloForestApplication.class)
public class MailSenderRunnerTest {
	@Autowired
	private MailSenderRunner mailSender;

	@Test
	public void sendEmail() {
		mailSender.sendMessage("test@test.com", "testSubject", "testContent");
	}
}
