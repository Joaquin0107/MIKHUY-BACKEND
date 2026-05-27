package pe.MIKHUY.ServiceImplements;

import pe.MIKHUY.Service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImplements implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("📧 EMAIL SERVICE - JavaMail (Gmail SMTP)");
        log.info("📧 From: {}", fromEmail);
        log.info("========================================");
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("📧 Enviando email simple a: {}", to);

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, false, "UTF-8");

            helper.setFrom("MIKHUY <" + fromEmail + ">");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, false);

            mailSender.send(message);
            log.info("✅ Email simple enviado a: {}", to);

        } catch (MessagingException e) {
            log.error("❌ Error enviando email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        MultipartFile attachment, String profesorNombre)
            throws MessagingException, IOException {

        log.info("📧 Enviando email con PDF a: {}", to);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom("MIKHUY <" + fromEmail + ">");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(buildHtmlContent(text, profesorNombre), true);
        helper.addAttachment(
                attachment.getOriginalFilename() != null
                        ? attachment.getOriginalFilename()
                        : "reporte.pdf",
                attachment
        );

        mailSender.send(message);
        log.info("✅ Email con PDF enviado a: {}", to);
    }

    private String buildHtmlContent(String message, String profesorNombre) {
        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return "<!DOCTYPE html>" +
                "<html><head><meta charset='UTF-8'></head>" +
                "<body style='font-family:Arial,sans-serif;background:#f8f9fa;padding:20px;margin:0;'>" +
                "<div style='max-width:600px;margin:0 auto;background:white;padding:30px;border-radius:10px;'>" +

                "<div style='text-align:center;background:linear-gradient(135deg,#48A3F3,#5bb3ff);padding:20px;border-radius:10px 10px 0 0;margin:-30px -30px 30px;'>" +
                "<h1 style='color:white;margin:0;font-size:26px;'>🎓 Plataforma MIKHUY</h1>" +
                "<p style='color:white;margin-top:8px;font-size:13px;opacity:.9;'>Reporte Nutricional del Estudiante</p>" +
                "</div>" +

                "<p><strong>Estimado padre/madre de familia,</strong></p>" +
                "<p>" + message + "</p>" +

                "<div style='background:#e3f2fd;padding:15px;border-radius:8px;margin:20px 0;border-left:4px solid #48A3F3;'>" +
                "<p style='margin:5px 0;font-weight:bold;'>📊 Contenido del reporte adjunto:</p>" +
                "<ul style='margin:10px 0;padding-left:20px;'>" +
                "<li>Evaluación nutricional completa</li>" +
                "<li>Gráficas de progreso por juegos educativos</li>" +
                "<li>Análisis de macronutrientes</li>" +
                "<li>Etapa de cambio conductual</li>" +
                "<li>Estadísticas de desempeño y ranking</li>" +
                "<li>Recomendaciones personalizadas</li>" +
                "</ul></div>" +

                "<div style='background:#fff3cd;padding:10px 15px;border-radius:5px;border-left:3px solid #ffc107;margin:15px 0;'>" +
                "<p style='margin:0;'><strong>📎 Archivo adjunto:</strong> Reporte Nutricional en formato PDF</p>" +
                "</div>" +

                "<div style='background:#f8f9fa;padding:15px;border-radius:5px;margin-top:20px;border-left:3px solid #48A3F3;font-size:14px;color:#666;'>" +
                "<p style='margin:5px 0;'><strong style='color:#48A3F3;'>Enviado por:</strong> " + profesorNombre + "</p>" +
                "<p style='margin:5px 0;'><strong style='color:#48A3F3;'>Fecha:</strong> " + fechaActual + "</p>" +
                "</div>" +

                "<div style='text-align:center;margin-top:30px;padding-top:20px;border-top:1px solid #e0e0e0;color:#999;font-size:12px;'>" +
                "<p>Correo automático generado por la Plataforma MIKHUY</p>" +
                "<p><strong>© 2026 MIKHUY</strong> — Todos los derechos reservados</p>" +
                "</div>" +
                "</div></body></html>";
    }
}