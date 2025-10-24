package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnviarCorreoRequest {

    // Credenciales del profesor para enviar el correo
    @NotBlank(message = "El email del profesor es obligatorio")
    @Email(message = "El formato del email es inválido")
    private String profesorEmail;

    @NotBlank(message = "La contraseña del email es obligatoria")
    private String profesorPassword;

    // Destinatarios
    @NotBlank(message = "El email del destinatario es obligatorio")
    @Email(message = "El formato del email del destinatario es inválido")
    private String destinatarioEmail;

    // Contenido del correo
    @NotBlank(message = "El asunto es obligatorio")
    private String asunto;

    private String mensaje;

    // Reporte asociado (opcional)
    private UUID reporteId;

    // Adjuntos (opcional)
    private String adjuntoUrl; // URL del PDF u otro archivo
}