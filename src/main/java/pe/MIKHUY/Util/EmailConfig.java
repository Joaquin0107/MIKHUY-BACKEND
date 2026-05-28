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
        log.info("🔧 Creando MailSender personalizado para: {}", email);

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
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");

        log.info("✅ MailSender personalizado creado para: {}", email);
        return mailSender;
    }
}