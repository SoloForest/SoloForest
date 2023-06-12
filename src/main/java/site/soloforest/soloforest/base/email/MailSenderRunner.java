package site.soloforest.soloforest.base.email;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class MailSenderRunner implements ApplicationRunner {

	private final JavaMailSender mailSender;

	@Value("${spring.mail.username}")
	private String from;

	@Override
	public void run(ApplicationArguments args) throws Exception {
		MimeMessage m = mailSender.createMimeMessage();
		MimeMessageHelper h = new MimeMessageHelper(m, "UTF-8");
		//h.setFrom(from);
		//h.setTo("");
		//h.setSubject("");
		//h.setText("");
		//mailSender.send(m);
	}

	public void sendMessage(String to, String subject, String text) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom(from);
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		mailSender.send(message);
	}
}
