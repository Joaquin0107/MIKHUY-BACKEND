package pe.MIKHUY.Controllers;

import pe.MIKHUY.Service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "*")
public class EmailController {

    @Autowired
    private EmailService emailService; // ✅ Usa la interfaz

    /**
     * Enviar email simple
     */
    @PostMapping("/send")
    public ResponseEntity<?> sendEmail(@RequestBody Map<String, String> emailData) {
        try {
            String to = emailData.get("to");
            String subject = emailData.get("subject");
            String message = emailData.get("message");

            emailService.sendSimpleEmail(to, subject, message);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Correo enviado exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al enviar el correo: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    /**
     * Enviar email con PDF adjunto
     */
    @PostMapping("/send-with-pdf")
    public ResponseEntity<?> sendEmailWithPdf(
            @RequestParam("to") String to,
            @RequestParam("subject") String subject,
            @RequestParam("message") String message,
            @RequestParam("profesorNombre") String profesorNombre,
            @RequestParam("pdf") MultipartFile pdfFile) {
        try {
            // Validar que el archivo sea PDF
            if (pdfFile.getContentType() == null ||
                    !pdfFile.getContentType().equals("application/pdf")) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El archivo debe ser un PDF");
                return ResponseEntity.badRequest().body(response);
            }

            // Validar que el archivo no esté vacío
            if (pdfFile.isEmpty()) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "El archivo PDF está vacío");
                return ResponseEntity.badRequest().body(response);
            }

            emailService.sendEmailWithAttachment(to, subject, message, pdfFile, profesorNombre);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Correo con PDF enviado exitosamente");

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Error al enviar el correo: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}