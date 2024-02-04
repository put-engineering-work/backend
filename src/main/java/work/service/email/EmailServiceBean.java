package work.service.email;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceBean implements EmailService {
    @Value("${is.email.sending.active}")
    private Boolean isEmailSendingActive;
    private final JavaMailSender javaMailSender;
    @Value("${spring.mail.username}")
    private String sender;

    public EmailServiceBean(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    @Override
    @Async
    public void emailConfirmation(EmailDetails emailDetails) {
        if (isEmailSendingActive) {
            try {
                SimpleMailMessage mailMessage
                        = new SimpleMailMessage();

                mailMessage.setFrom(sender);
                mailMessage.setTo(emailDetails.recipient());
                mailMessage.setText(emailDetails.msgBody());
                mailMessage.setSubject(emailDetails.subject());

                javaMailSender.send(mailMessage);
            } catch (Exception ignored) {

            }
        }
    }
}
