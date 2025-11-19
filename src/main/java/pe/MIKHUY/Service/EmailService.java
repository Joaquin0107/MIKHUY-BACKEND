package pe.MIKHUY.Service;

import jakarta.mail.MessagingException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

/**
 * Servicio para el envío de correos electrónicos
 */
public interface EmailService {

    /**
     * Enviar email simple sin adjuntos
     *
     * @param to Email del destinatario
     * @param subject Asunto del correo
     * @param text Contenido del mensaje
     */
    void sendSimpleEmail(String to, String subject, String text);

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
    void sendEmailWithAttachment(String to, String subject, String text,
                                 MultipartFile attachment, String profesorNombre)
            throws MessagingException, IOException;
}