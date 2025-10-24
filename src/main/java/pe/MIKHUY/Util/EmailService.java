package pe.MIKHUY.Util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import pe.MIKHUY.Entities.CorreoEnviado;
import pe.MIKHUY.Entities.Profesor;
import pe.MIKHUY.Repositories.CorreoEnviadoRepository;

import java.io.File;
import java.time.LocalDateTime;

/**
 * Servicio para envío de correos electrónicos
 * Permite que profesores envíen correos con sus credenciales personales
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender defaultMailSender;
    private final EmailConfig emailConfig;
    private final CorreoEnviadoRepository correoEnviadoRepository;

    /**
     * Enviar email simple (texto plano) con el servidor por defecto
     */
    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);

            defaultMailSender.send(message);

            log.info("Email enviado exitosamente a: {}", to);

            // Guardar registro
            guardarRegistroCorreo(null, to, subject, text, null, CorreoEnviado.EstadoCorreoEnum.enviado);

        } catch (Exception e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            guardarRegistroCorreo(null, to, subject, text, null, CorreoEnviado.EstadoCorreoEnum.fallido);
            throw new RuntimeException("Error al enviar email: " + e.getMessage());
        }
    }

    /**
     * Enviar email HTML con adjuntos usando credenciales del profesor
     * Esto permite que el profesor envíe desde su propio correo
     */
    public void sendEmailFromProfesor(
            Profesor profesor,
            String profesorEmail,
            String profesorPassword,
            String to,
            String subject,
            String htmlContent,
            String attachmentPath
    ) {
        try {
            // Crear JavaMailSender con credenciales del profesor
            JavaMailSender profesorMailSender = emailConfig.createCustomMailSender(
                    profesorEmail,
                    profesorPassword,
                    "smtp.gmail.com", // Cambiar según proveedor
                    587
            );

            // Crear mensaje MIME
            MimeMessage message = profesorMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(profesorEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML

            // Agregar adjunto si existe
            if (attachmentPath != null && !attachmentPath.isEmpty()) {
                FileSystemResource file = new FileSystemResource(new File(attachmentPath));
                helper.addAttachment(file.getFilename(), file);
            }

            // Enviar
            profesorMailSender.send(message);

            log.info("Email enviado desde profesor {} a: {}", profesorEmail, to);

            // Guardar registro
            guardarRegistroCorreo(null, to, subject, htmlContent, attachmentPath, CorreoEnviado.EstadoCorreoEnum.enviado);

        } catch (MessagingException e) {
            log.error("Error al enviar email desde profesor: {}", e.getMessage());
            guardarRegistroCorreo(null, to, subject, htmlContent, attachmentPath, CorreoEnviado.EstadoCorreoEnum.fallido);
            throw new RuntimeException("Error al enviar email: " + e.getMessage());
        }
    }

    /**
     * Enviar email con reporte PDF adjunto
     */
    public void sendReportEmail(
            String profesorEmail,
            String profesorPassword,
            String to,
            String subject,
            String studentName,
            String pdfPath
    ) {
        String htmlContent = buildReportEmailHtml(studentName);

        sendEmailFromProfesor(
                null,
                profesorEmail,
                profesorPassword,
                to,
                subject,
                htmlContent,
                pdfPath
        );
    }

    /**
     * Construir HTML para email de reporte
     */
    private String buildReportEmailHtml(String studentName) {
        return """
            <html>
            <body style='font-family: Arial, sans-serif;'>
                <div style='max-width: 600px; margin: 0 auto; padding: 20px;'>
                    <h2 style='color: #2c3e50;'>Reporte Nutricional - MIKHUY</h2>
                    <p>Estimado padre/madre de familia,</p>
                    <p>Adjunto encontrará el reporte nutricional de <strong>%s</strong>.</p>
                    <p>Este reporte contiene información detallada sobre:</p>
                    <ul>
                        <li>Progreso en juegos educativos</li>
                        <li>Análisis nutricional</li>
                        <li>Recomendaciones personalizadas</li>
                        <li>Estadísticas de participación</li>
                    </ul>
                    <p>Para cualquier consulta, no dude en contactarnos.</p>
                    <br>
                    <p>Atentamente,</p>
                    <p><strong>Equipo MIKHUY</strong></p>
                    <hr style='border: 1px solid #ecf0f1;'>
                    <p style='font-size: 12px; color: #7f8c8d;'>
                        Este es un mensaje automático, por favor no responder a este correo.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(studentName);
    }

    /**
     * Validar credenciales de email del profesor
     */
    public boolean validateEmailCredentials(String email, String password) {
        try {
            JavaMailSender testSender = emailConfig.createCustomMailSender(email, password, "smtp.gmail.com", 587);

            // Intentar crear un mensaje (no se envía)
            MimeMessage message = testSender.createMimeMessage();

            log.info("Credenciales de email validadas correctamente para: {}", email);
            return true;

        } catch (Exception e) {
            log.error("Error validando credenciales de email: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Guardar registro de correo enviado en la BD
     */
    private void guardarRegistroCorreo(
            pe.MIKHUY.Entities.Reporte reporte,
            String destinatario,
            String asunto,
            String mensaje,
            String adjuntoUrl,
            CorreoEnviado.EstadoCorreoEnum estado
    ) {
        try {
            CorreoEnviado correo = new CorreoEnviado();
            correo.setReporte(reporte);
            correo.setDestinatarioEmail(destinatario);
            correo.setAsunto(asunto);
            correo.setMensaje(mensaje);
            correo.setAdjuntoUrl(adjuntoUrl);
            correo.setEstado(estado);
            correo.setFechaEnvio(LocalDateTime.now());

            correoEnviadoRepository.save(correo);
        } catch (Exception e) {
            log.error("Error al guardar registro de correo: {}", e.getMessage());
        }
    }
}