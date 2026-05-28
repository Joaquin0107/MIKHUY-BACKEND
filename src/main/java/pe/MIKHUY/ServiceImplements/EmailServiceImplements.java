package pe.MIKHUY.ServiceImplements;

import pe.MIKHUY.Service.EmailService;
import jakarta.annotation.PostConstruct;
import jakarta.mail.MessagingException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
@Slf4j
public class EmailServiceImplements implements EmailService {

    @Value("${resend.api.key}")
    private String resendApiKey;

    @Value("${resend.from.email:onboarding@resend.dev}")
    private String fromEmail;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String RESEND_API_URL = "https://api.resend.com/emails";

    @PostConstruct
    public void init() {
        log.info("========================================");
        log.info("📧 CONFIGURACIÓN DE EMAIL SERVICE (RESEND)");
        log.info("========================================");
        log.info("📧 From Email: {}", fromEmail);
        log.info("📧 API Key configurada: {}",
                resendApiKey != null && !resendApiKey.isEmpty() ? "✅ SÍ" : "❌ NO");
        log.info("========================================");
    }

    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("📧 Enviando email simple a: {}", to);

            HttpHeaders headers = createHeaders();

            Map<String, Object> body = new HashMap<>();
            body.put("from", "MIKHUY <" + fromEmail + ">");
            body.put("to", Collections.singletonList(to));
            body.put("subject", subject);
            body.put("text", text);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    RESEND_API_URL, request, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email enviado exitosamente");
            } else {
                throw new RuntimeException("Error de Resend: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Error al enviar email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email: " + e.getMessage(), e);
        }
    }

    @Override
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        MultipartFile attachment, String profesorNombre)
            throws MessagingException, IOException {
        try {
            log.info("📧 Enviando email con PDF a: {}", to);

            HttpHeaders headers = createHeaders();

            String pdfBase64 = Base64.getEncoder().encodeToString(attachment.getBytes());

            Map<String, String> attachmentMap = new HashMap<>();
            attachmentMap.put("filename", attachment.getOriginalFilename() != null
                    ? attachment.getOriginalFilename() : "reporte.pdf");
            attachmentMap.put("content", pdfBase64);

            Map<String, Object> body = new HashMap<>();
            body.put("from", "MIKHUY <" + fromEmail + ">");
            body.put("to", Collections.singletonList(to));
            body.put("subject", subject);
            body.put("html", buildHtmlContent(text, profesorNombre));
            body.put("attachments", Collections.singletonList(attachmentMap));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            ResponseEntity<String> response = restTemplate.postForEntity(
                    RESEND_API_URL, request, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email con PDF enviado exitosamente");
            } else {
                throw new RuntimeException("Error de Resend: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Error al enviar email con PDF: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email con PDF: " + e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + resendApiKey);
        return headers;
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