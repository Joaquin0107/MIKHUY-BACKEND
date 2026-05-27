package pe.MIKHUY.Controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import pe.MIKHUY.Service.EmailService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@RequiredArgsConstructor
@Slf4j
// ❌ NO usar @CrossOrigin aquí - ya está configurado en SecurityConfig
public class EmailController {

    private final EmailService emailService;

    /**
     * Enviar email simple
     * POST /api/email/send
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> emailData) {
        try {
            log.info("📧 Recibiendo solicitud de envío de email");

            String to = emailData.get("to");
            String subject = emailData.get("subject");
            String message = emailData.get("message");

            // Validaciones
            if (to == null || to.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El destinatario es requerido"));
            }
            if (subject == null || subject.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El asunto es requerido"));
            }
            if (message == null || message.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El mensaje es requerido"));
            }

            log.info("📧 Enviando email a: {}", to);
            emailService.sendSimpleEmail(to, subject, message);
            log.info("✅ Email enviado exitosamente");

            return ResponseEntity.ok(createSuccessResponse("Correo enviado exitosamente"));

        } catch (Exception e) {
            log.error("❌ Error al enviar email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error al enviar el correo: " + e.getMessage()));
        }
    }

    /**
     * Enviar email con PDF adjunto
     * POST /api/email/send-with-pdf
     */
    @PostMapping("/send-with-pdf")
    public ResponseEntity<?> sendEmailWithPdf(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            @RequestParam("profesorNombre") String profesorNombre,
            @RequestParam("pdf") MultipartFile pdfFile) {
        try {
            log.info("📧 Recibiendo solicitud de envío de email con PDF");

            // Validar destinatario
            if (to == null || to.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El destinatario es requerido"));
            }

            // Validar archivo PDF
            if (pdfFile == null || pdfFile.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("El archivo PDF es requerido"));
            }

            // Validar tipo de archivo
            String contentType = pdfFile.getContentType();
            if (contentType == null || !contentType.equals("application/pdf")) {
                return ResponseEntity.badRequest().body(
                        createErrorResponse("El archivo debe ser un PDF. Tipo recibido: " + contentType)
                );
            }

            // Validar tamaño del archivo (máx 10MB)
            long maxSize = 10 * 1024 * 1024; // 10MB
            if (pdfFile.getSize() > maxSize) {
                return ResponseEntity.badRequest().body(
                        createErrorResponse("El archivo PDF es demasiado grande. Máximo: 10MB")
                );
            }

            log.info("📧 Enviando email con PDF a: {}", to);
            log.info("📄 Archivo: {} ({} bytes)", pdfFile.getOriginalFilename(), pdfFile.getSize());

            emailService.sendEmailWithAttachment(to, subject, message, pdfFile, profesorNombre);

            log.info("✅ Email con PDF enviado exitosamente");

            return ResponseEntity.ok(createSuccessResponse("Correo con PDF enviado exitosamente"));

        } catch (Exception e) {
            log.error("❌ Error al enviar email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error: " + e.getMessage() +
                            " | Causa: " + (e.getCause() != null ? e.getCause().getMessage() : "sin causa")));
        }
    }

    /**
     * Método auxiliar para crear respuestas de éxito
     */
    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    /**
     * Método auxiliar para crear respuestas de error
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}