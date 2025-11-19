package pe.MIKHUY.ServiceImplements;

import pe.MIKHUY.Service.EmailService;
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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementación del servicio de correos electrónicos
 * Esta clase contiene la lógica real para enviar correos
 */
@Service
public class EmailServiceImplements implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * Enviar email simple sin adjuntos
     *
     * @param to Email del destinatario
     * @param subject Asunto del correo
     * @param text Contenido del mensaje
     */
    @Override
    public void sendSimpleEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(fromEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    /**
     * Enviar email con archivo PDF adjunto
     *
     * @param to Email del destinatario
     * @param subject Asunto del correo
     * @param text Contenido del mensaje
     * @param attachment Archivo PDF adjunto
     * @param profesorNombre Nombre del profesor que envía el correo
     * @throws MessagingException Si hay error en el envío
     * @throws IOException Si hay error leyendo el archivo
     */
    @Override
    public void sendEmailWithAttachment(String to, String subject, String text,
                                        MultipartFile attachment, String profesorNombre)
            throws MessagingException, IOException {

        // Crear mensaje MIME (permite HTML y adjuntos)
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        // Configurar remitente y destinatario
        helper.setFrom(fromEmail, "Plataforma MIKHUY");
        helper.setTo(to);
        helper.setSubject(subject);

        // Crear el cuerpo del email con HTML
        String htmlContent = buildHtmlContent(text, profesorNombre);
        helper.setText(htmlContent, true); // true = es HTML

        // Adjuntar el PDF
        String fileName = attachment.getOriginalFilename();
        ByteArrayResource resource = new ByteArrayResource(attachment.getBytes());
        helper.addAttachment(fileName, resource, "application/pdf");

        // Enviar el correo
        mailSender.send(message);
    }

    /**
     * Construir contenido HTML personalizado para el email
     *
     * @param message Mensaje personalizado del profesor
     * @param profesorNombre Nombre del profesor que envía
     * @return String con el HTML completo del email
     */
    private String buildHtmlContent(String message, String profesorNombre) {
        // Obtener fecha y hora actual
        String fechaActual = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"));

        return "<!DOCTYPE html>" +
                "<html>" +
                "<head>" +
                "<meta charset='UTF-8'>" +
                "<meta name='viewport' content='width=device-width, initial-scale=1.0'>" +
                "<style>" +
                "body { " +
                "    font-family: 'Arial', sans-serif; " +
                "    background-color: #f8f9fa; " +
                "    padding: 20px; " +
                "    margin: 0; " +
                "}" +
                ".container { " +
                "    max-width: 600px; " +
                "    margin: 0 auto; " +
                "    background: white; " +
                "    padding: 30px; " +
                "    border-radius: 10px; " +
                "    box-shadow: 0 2px 10px rgba(0,0,0,0.1); " +
                "}" +
                ".header { " +
                "    text-align: center; " +
                "    margin-bottom: 30px; " +
                "    background: linear-gradient(135deg, #48A3F3 0%, #5bb3ff 100%); " +
                "    padding: 20px; " +
                "    border-radius: 10px 10px 0 0; " +
                "    margin: -30px -30px 30px -30px; " +
                "}" +
                ".header h1 { " +
                "    color: white; " +
                "    margin: 0; " +
                "    font-size: 28px; " +
                "}" +
                ".header p { " +
                "    color: white; " +
                "    margin-top: 10px; " +
                "    opacity: 0.9; " +
                "    font-size: 14px; " +
                "}" +
                ".content { " +
                "    color: #333; " +
                "    line-height: 1.8; " +
                "    margin: 20px 0; " +
                "}" +
                ".content p { " +
                "    margin: 10px 0; " +
                "}" +
                ".info-box { " +
                "    background: #e3f2fd; " +
                "    padding: 15px; " +
                "    border-radius: 8px; " +
                "    margin: 20px 0; " +
                "    border-left: 4px solid #48A3F3; " +
                "}" +
                ".info-box p { " +
                "    margin: 5px 0; " +
                "    font-weight: bold; " +
                "    color: #333; " +
                "}" +
                ".info-box ul { " +
                "    margin: 10px 0; " +
                "    padding-left: 20px; " +
                "}" +
                ".info-box li { " +
                "    margin: 5px 0; " +
                "    color: #555; " +
                "}" +
                ".footer { " +
                "    text-align: center; " +
                "    margin-top: 30px; " +
                "    padding-top: 20px; " +
                "    border-top: 1px solid #e0e0e0; " +
                "    color: #999; " +
                "    font-size: 12px; " +
                "}" +
                ".footer p { " +
                "    margin: 5px 0; " +
                "}" +
                ".profesor-info { " +
                "    background: #f8f9fa; " +
                "    padding: 15px; " +
                "    border-radius: 5px; " +
                "    margin-top: 20px; " +
                "    font-size: 14px; " +
                "    color: #666; " +
                "    border-left: 3px solid #48A3F3; " +
                "}" +
                ".profesor-info p { " +
                "    margin: 5px 0; " +
                "}" +
                ".highlight { " +
                "    color: #48A3F3; " +
                "    font-weight: bold; " +
                "}" +
                ".attachment-icon { " +
                "    background: #fff3cd; " +
                "    padding: 10px 15px; " +
                "    border-radius: 5px; " +
                "    border-left: 3px solid #ffc107; " +
                "    margin: 15px 0; " +
                "}" +
                "</style>" +
                "</head>" +
                "<body>" +
                "<div class='container'>" +

                // HEADER
                "<div class='header'>" +
                "<h1>🎓 Plataforma MIKHUY</h1>" +
                "<p>Reporte Nutricional del Estudiante</p>" +
                "</div>" +

                // CONTENT
                "<div class='content'>" +
                "<p><strong>Estimado padre/madre de familia,</strong></p>" +
                "<p>" + message + "</p>" +

                // INFO BOX
                "<div class='info-box'>" +
                "<p>📊 Contenido del reporte adjunto:</p>" +
                "<ul>" +
                "<li>Evaluación nutricional completa</li>" +
                "<li>Gráficas de progreso por juegos educativos</li>" +
                "<li>Análisis de macronutrientes</li>" +
                "<li>Etapa de cambio conductual</li>" +
                "<li>Estadísticas de desempeño y ranking</li>" +
                "<li>Recomendaciones personalizadas</li>" +
                "</ul>" +
                "</div>" +

                // ATTACHMENT INFO
                "<div class='attachment-icon'>" +
                "<p style='margin: 0;'><strong>📎 Archivo adjunto:</strong> Reporte Nutricional en formato PDF</p>" +
                "</div>" +

                // PROFESOR INFO
                "<div class='profesor-info'>" +
                "<p><strong class='highlight'>Enviado por:</strong> " + profesorNombre + "</p>" +
                "<p><strong class='highlight'>Fecha de envío:</strong> " + fechaActual + "</p>" +
                "</div>" +
                "</div>" +

                // FOOTER
                "<div class='footer'>" +
                "<p>Este es un correo automático generado por la Plataforma MIKHUY</p>" +
                "<p>Para consultas adicionales, contacte directamente con su profesor(a)</p>" +
                "<p><strong>© 2024 MIKHUY</strong> - Todos los derechos reservados</p>" +
                "</div>" +

                "</div>" +
                "</body>" +
                "</html>";
    }
}