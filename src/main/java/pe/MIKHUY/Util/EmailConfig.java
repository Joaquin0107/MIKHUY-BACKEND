package pe.MIKHUY.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración de JavaMailSender para envío de correos
 */
@Configuration
@Slf4j
public class EmailConfig {

    @Value("${spring.mail.host:smtp.gmail.com}")
    private String mailHost;

    @Value("${spring.mail.port:587}")
    private Integer mailPort;

    @Value("${spring.mail.username:profesor.mikhuy@gmail.com}")
    private String mailUsername;

    @Value("${spring.mail.password:qgzihifxfexjqphv}")
    private String mailPassword;

    /**
     * Bean de JavaMailSender configurado con Gmail
     */
    @Bean
    public JavaMailSender javaMailSender() {
        log.info("========================================");
        log.info("🔧 CONFIGURANDO JAVA MAIL SENDER");
        log.info("========================================");

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // ✅ Configuración del servidor SMTP
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);
        mailSender.setUsername(mailUsername);
        mailSender.setPassword(mailPassword);

        log.info("✅ Host: {}", mailHost);
        log.info("✅ Port: {}", mailPort);
        log.info("✅ Username: {}", mailUsername);
        log.info("✅ Password: {}****", mailPassword.substring(0, 4));

        // ✅ Propiedades SMTP
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", mailHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "true");
        props.put("mail.smtp.connectiontimeout", "5000");
        props.put("mail.smtp.timeout", "5000");
        props.put("mail.smtp.writetimeout", "5000");

        log.info("✅ Propiedades SMTP configuradas");
        log.info("✅ STARTTLS: habilitado");
        log.info("✅ Autenticación: habilitada");
        log.info("========================================");

        return mailSender;
    }

    /**
     * Crear JavaMailSender con credenciales personalizadas del profesor
     * Esto permite que cada profesor envíe correos con su propio email
     */
    public JavaMailSender createCustomMailSender(String email, String password, String host, Integer port) {
        log.info("🔧 Creando MailSender personalizado para: {}", email);

        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Configuración del servidor SMTP
        String smtpHost = host != null ? host : "smtp.gmail.com";
        Integer smtpPort = port != null ? port : 587;

        mailSender.setHost(smtpHost);
        mailSender.setPort(smtpPort);
        mailSender.setUsername(email);
        mailSender.setPassword(password);

        // Propiedades adicionales
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", smtpHost);
        props.put("mail.smtp.ssl.protocols", "TLSv1.2");
        props.put("mail.debug", "false");

        log.info("✅ MailSender personalizado creado para: {}", email);

        return mailSender;
    }
}