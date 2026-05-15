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

/**
 * Implementación del servicio de correos electrónicos usando Resend API
 * Resend usa HTTP en lugar de SMTP, funciona en Render y otros servicios cloud
 */
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

    /**
     * Enviar email simple sin adjuntos
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            log.info("📧 ========== ENVIANDO EMAIL SIMPLE ==========");
            log.info("📧 To: {}", to);
            log.info("📧 From: {}", fromEmail);
            log.info("📧 Subject: {}", subject);

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
                log.info("📧 Response: {}", response.getBody());
            } else {
                throw new RuntimeException("Error de Resend: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ Error al enviar email: {}", e.getMessage());
            throw new RuntimeException("Error al enviar email: " + e.getMessage(), e);
        }
    }

    /**
     * Enviar email con archivo PDF adjunto
     */
    @Override
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        MultipartFile attachment, String profesorNombre)
            throws MessagingException, IOException {

        try {
            log.info("📧 ========== ENVIANDO EMAIL CON PDF (RESEND) ==========");
            log.info("📧 To: {}", to);
            log.info("📧 From: {}", fromEmail);
            log.info("📧 Subject: {}", subject);
            log.info("📧 Attachment: {} ({} bytes)",
                    attachment.getOriginalFilename(),
                    attachment.getSize());
            log.info("📧 Profesor: {}", profesorNombre);

            HttpHeaders headers = createHeaders();

            // Convertir PDF a Base64
            String pdfBase64 = Base64.getEncoder().encodeToString(attachment.getBytes());

            // Crear el adjunto
            Map<String, String> attachmentMap = new HashMap<>();
            attachmentMap.put("filename", attachment.getOriginalFilename());
            attachmentMap.put("content", pdfBase64);

            // Crear el cuerpo del request
            Map<String, Object> body = new HashMap<>();
            body.put("from", "MIKHUY <" + fromEmail + ">");
            body.put("to", Collections.singletonList(to));
            body.put("subject", subject);
            body.put("html", buildHtmlContent(text, profesorNombre));
            body.put("attachments", Collections.singletonList(attachmentMap));

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

            log.info("📧 Enviando request a Resend API...");

            ResponseEntity<String> response = restTemplate.postForEntity(
                    RESEND_API_URL, request, String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("✅ Email con PDF enviado exitosamente");
                log.info("📧 Response: {}", response.getBody());
            } else {
                log.error("❌ Error de Resend: {}", response.getBody());
                throw new RuntimeException("Error de Resend: " + response.getBody());
            }

        } catch (Exception e) {
            log.error("❌ ========== ERROR AL ENVIAR EMAIL CON PDF ==========");
            log.error("❌ Error: {}", e.getMessage());
            log.error("❌ Stack trace:", e);
            throw new RuntimeException("Error al enviar email con PDF: " + e.getMessage(), e);
        }
    }

    /**
     * Crear headers para la API de Resend
     */
    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("Authorization", "Bearer " + resendApiKey);
        return headers;
    }

    /**
     * Construir contenido HTML personalizado para el email
     */
    private String buildHtmlContent(String message, String profesorNombre) {
        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "</head>" +
                "<body style='font-family: Arial, sans-serif; background-color: #f8f9fa; padding: 20px; margin: 0;'>" +
                "<div style='max-width: 600px; margin: 0 auto; background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1);'>" +

                // HEADER
                "<div style='text-align: center; margin-bottom: 30px; background: linear-gradient(135deg, #48A3F3 0%, #5bb3ff 100%); padding: 20px; border-radius: 10px 10px 0 0; margin: -30px -30px 30px -30px;'>" +
                "<h1 style='color: white; margin: 0; font-size: 28px;'>🎓 Plataforma MIKHUY</h1>" +
                "<p style='color: white; margin-top: 10px; opacity: 0.9; font-size: 14px;'>Reporte Nutricional del Estudiante</p>" +
                "</div>" +

                // CONTENT
                "<div style='color: #333; line-height: 1.8; margin: 20px 0;'>" +
                "<p><strong>Estimado padre/madre de familia,</strong></p>" +
                "<p>" + message + "</p>" +

                // INFO BOX
                "<div style='background: #e3f2fd; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #48A3F3;'>" +
                "<p style='margin: 5px 0; font-weight: bold; color: #333;'>📊 Contenido del reporte adjunto:</p>" +
                "<ul style='margin: 10px 0; padding-left: 20px;'>" +
                "<li style='margin: 5px 0; color: #555;'>Evaluación nutricional completa</li>" +
                "<li style='margin: 5px 0; color: #555;'>Gráficas de progreso por juegos educativos</li>" +
                "<li style='margin: 5px 0; color: #555;'>Análisis de macronutrientes</li>" +
                "<li style='margin: 5px 0; color: #555;'>Etapa de cambio conductual</li>" +
                "<li style='margin: 5px 0; color: #555;'>Estadísticas de desempeño y ranking</li>" +
                "<li style='margin: 5px 0; color: #555;'>Recomendaciones personalizadas</li>" +
                "</ul>" +
                "</div>" +

                // ATTACHMENT INFO
                "<div style='background: #fff3cd; padding: 10px 15px; border-radius: 5px; border-left: 3px solid #ffc107; margin: 15px 0;'>" +
                "<p style='margin: 0;'><strong>📎 Archivo adjunto:</strong> Reporte Nutricional en formato PDF</p>" +
                "</div>" +

                // PROFESOR INFO
                "<div style='background: #f8f9fa; padding: 15px; border-radius: 5px; margin-top: 20px; font-size: 14px; color: #666; border-left: 3px solid #48A3F3;'>" +
                "<p style='margin: 5px 0;'><strong style='color: #48A3F3;'>Enviado por:</strong> " + profesorNombre + "</p>" +
                "<p style='margin: 5px 0;'><strong style='color: #48A3F3;'>Fecha de envío:</strong> " + fechaActual + "</p>" +
                "</div>" +
                "</div>" +

                // FOOTER
                "<div style='text-align: center; margin-top: 30px; padding-top: 20px; border-top: 1px solid #e0e0e0; color: #999; font-size: 12px;'>" +
                "<p style='margin: 5px 0;'>Este es un correo automático generado por la Plataforma MIKHUY</p>" +
                "<p style='margin: 5px 0;'>Para consultas adicionales, contacte directamente con su profesor(a)</p>" +
                "<p style='margin: 5px 0;'><strong>© 2025 MIKHUY</strong> - Todos los derechos reservados</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }
}