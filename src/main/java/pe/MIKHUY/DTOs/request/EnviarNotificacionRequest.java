package pe.MIKHUY.DTOs.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

/**
 * Request para enviar una notificación de amistad a otro estudiante.
 *
 * Tipos válidos:
 *   "amistad_solicitud"  → A le pide amistad a B
 *   "amistad_aceptada"   → B acepta la solicitud de A
 *   "amistad_rechazada"  → B rechaza la solicitud de A
 */
@Data
public class EnviarNotificacionRequest {

    /**
     * ID del estudiante destinatario (no el usuarioId, sino el estudianteId
     * que devuelve GET /api/amigos/compañeros → campo "id").
     */
    @NotNull(message = "El ID del destinatario es requerido")
    private UUID destinatarioEstudianteId;

    /**
     * Tipo de notificación. Uno de: amistad_solicitud, amistad_aceptada, amistad_rechazada.
     */
    @NotBlank(message = "El tipo de notificación es requerido")
    private String tipo;

    /**
     * Mensaje que verá el destinatario en su panel de notificaciones.
     * Ejemplo: "Juan Pérez te envió una solicitud de amistad"
     */
    @NotBlank(message = "El mensaje es requerido")
    private String mensaje;

    /**
     * Nombre del remitente (se incluye para que el destinatario pueda
     * mostrar quién envió la solicitud sin hacer una llamada extra).
     */
    @NotBlank(message = "El nombre del remitente es requerido")
    private String nombreRemitente;

    /**
     * ID del estudiante remitente (el propio usuario autenticado).
     * El front lo envía para que el destinatario sepa a quién aceptar/rechazar.
     */
    @NotNull(message = "El ID del remitente es requerido")
    private UUID remitenteEstudianteId;
}