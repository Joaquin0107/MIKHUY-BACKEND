package com.mikhuy.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Enviar email simple sin adjuntos
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    /**
     * Enviar email con archivo adjunto
     */
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        MultipartFile attachment,
                                        String profesorNombre)
            throws MessagingException, IOException {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail, "Plataforma MIKHUY");
        helper.setTo(to);
        helper.setSubject(subject);

        // Crear el cuerpo del email con HTML incluyendo nombre del profesor
        String htmlContent = buildHtmlContent(text, profesorNombre);
        helper.setText(htmlContent, true);

        // Adjuntar el PDF
        String fileName = attachment.getOriginalFilename();
        ByteArrayResource resource = new ByteArrayResource(attachment.getBytes());
        helper.addAttachment(fileName, resource, "application/pdf");

        mailSender.send(message);
    }

    /**
     * Construir contenido HTML para el email
     */
    private String buildHtmlContent(String message, String profesorNombre) {
        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<style>" +
                "body { font-family: 'Arial', sans-serif; background-color: #f8f9fa; padding: 20px; }" +
                ".container { max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }" +
                ".header { text-align: center; margin-bottom: 30px; background: linear-gradient(135deg, #48A3F3 0%, #5bb3ff 100%); padding: 20px; border-radius: 10px 10px 0 0; margin: -30px -30px 30px -30px; }" +
                ".header h1 { color: white; margin: 0; font-size: 28px; }" +
                ".header p { color: white; margin-top: 10px; opacity: 0.9; }" +
                ".content { color: #333; line-height: 1.8; margin: 20px 0; }" +
                ".info-box { background: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #48A3F3; }" +
                ".footer { text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #999; font-size: 12px; }" +
                ".profesor-info { background: #f8f9fa; padding: 10px; border-radius: 5px; margin-top: 20px; font-size: 14px; color: #666; }" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +
                "<div class='header'>" +
                "<h1>🎓 Plataforma MIKHUY</h1>" +
                "<p>Reporte Nutricional del Estudiante</p>" +
                "</div>" +
                "<div class='content'>" +
                "<p>Estimado padre/madre de familia,</p>" +
                "<p>" + message + "</p>" +
                "<div class='info-box'>" +
                "<p><strong>📊 Contenido del reporte adjunto:</strong></p>" +
                "<ul style='margin: 10px 0;'>" +
                "<li>Evaluación nutricional completa</li>" +
                "<li>Gráficas de progreso por juegos</li>" +
                "<li>Análisis de macronutrientes</li>" +
                "<li>Etapa de cambio conductual</li>" +
                "<li>Estadísticas y ranking</li>" +
                "<li>Recomendaciones personalizadas</li>" +
                "</ul>" +
                "</div>" +
                "<p><strong>📎 Archivo adjunto:</strong> Reporte Nutricional en formato PDF</p>" +
                "<div class='profesor-info'>" +
                "<p style='margin: 0;'><strong>Enviado por:</strong> " + profesorNombre + "</p>" +
                "<p style='margin: 5px 0 0 0;'><strong>Fecha:</strong> " +
                java.time.LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) +
                "</p>" +
                "</div>" +
                "</div>" +
                "<div class='footer'>" +
                "<p>Este es un correo automático generado por la Plataforma MIKHUY</p>" +
                "<p>Para consultas, contacte a su profesor(a)</p>" +
                "<p>© 2024 MIKHUY - Todos los derechos reservados</p>" +
                "</div>" +
                "</div>" +
                "</body>" +
                "</html>";
    }
}