package pe.MIKHUY.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
@Slf4j
public class EmailConfig {

    public JavaMailSender createCustomMailSender(String email, String password, String host, Integer port) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        String smtpHost = host != null ? host : "smtp.gmail.com";
        Integer smtpPort = port != null ? port : 587;
        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(email);
        mailSender.setPassword(password);
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        return mailSender;
    }
}