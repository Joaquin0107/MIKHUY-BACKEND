package pe.MIKHUY.Util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

/**
 * Configuración de JavaMailSender para envío de correos
 */
@Configuration
public class EmailConfig {

    /**
     * Bean de JavaMailSender por defecto (usando configuración de application.properties)
     */
    @Bean
    public JavaMailSender javaMailSender() {
        return new JavaMailSenderImpl();
    }

    /**
     * Crear JavaMailSender con credenciales personalizadas del profesor
     * Esto permite que cada profesor envíe correos con su propio email
     */
    public JavaMailSender createCustomMailSender(String email, String password, String host, Integer port) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // Configuración del servidor SMTP
        mailSender.setHost(host != null ? host : "smtp.gmail.com");
        mailSender.setPort(port != null ? port : 587);
        mailSender.setUsername(email);
        mailSender.setPassword(password);

        // Propiedades adicionales
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.ssl.trust", host != null ? host : "smtp.gmail.com");
        props.put("mail.debug", "false"); // Cambiar a true para debug

        return mailSender;
    }
}