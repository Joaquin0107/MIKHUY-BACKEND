package pe.MIKHUY.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.DTOs.request.EnviarNotificacionRequest;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.Entities.Notificacion;
import pe.MIKHUY.Entities.Usuario;
import pe.MIKHUY.Repositories.NotificacionRepository;
import pe.MIKHUY.Repositories.UsuarioRepository;
import pe.MIKHUY.Repositories.AmistadRepository;
import pe.MIKHUY.Entities.Amistad;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * AmigoService
 *
 * Gestiona:
 *  1. Listar compañeros del mismo grado+sección (para el tab Amigos).
 *  2. Crear notificaciones de amistad entre estudiantes
 *     (solicitud / aceptada / rechazada).
 *
 * El estado de amistad (quién es amigo de quién) se persiste en localStorage
 * del navegador. El back solo actúa como canal de notificaciones.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AmigoService {

    private final EstudianteService estudianteService;
    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final AmistadRepository amistadRepository;

    private static final List<String> TIPOS_VALIDOS = List.of(
            "amistad_solicitud",
            "amistad_aceptada",
            "amistad_rechazada"
    );

    // ─── Compañeros ──────────────────────────────────────────────────────────

    /**
     * Devuelve estudiantes del mismo grado+sección, excluyendo al autenticado.
     */
    public List<EstudianteResponse> getCompanerosMismoGrupo(UUID usuarioId) {
        EstudianteResponse yo = estudianteService.getPerfilByUsuarioId(usuarioId);

        String miGrado   = yo.getGrado();
        String miSeccion = yo.getSeccion();
        UUID   miId      = yo.getId();

        log.info("🔍 Compañeros de grado={} sección={} para estudiante={}", miGrado, miSeccion, miId);

        return estudianteService.getAll().stream()
                .filter(e -> miGrado.equalsIgnoreCase(e.getGrado()))
                .filter(e -> miSeccion.equalsIgnoreCase(e.getSeccion()))
                .filter(e -> !miId.equals(e.getId()))
                .collect(Collectors.toList());
    }

    // ─── Notificaciones de amistad ────────────────────────────────────────────

    /**
     * Crea una notificación de amistad en la tabla existente.
     *
     * Estrategia para pasar el remitenteEstudianteId al front sin alterar
     * la entidad Notificacion: se codifica al final del mensaje separado por "|":
     *
     *   mensaje = "Juan Pérez te envió una solicitud de amistad|<remitenteEstudianteId>"
     *
     * El front separa por "|" y usa la segunda parte como remitenteId.
     */
    public void enviarNotificacion(UUID remitenteUsuarioId, EnviarNotificacionRequest request) {
        if (!TIPOS_VALIDOS.contains(request.getTipo())) {
            throw new IllegalArgumentException("Tipo inválido. Permitidos: " + TIPOS_VALIDOS);
        }

        // Verificar que el remitenteEstudianteId del request coincide con el token
        EstudianteResponse remitente = estudianteService.getPerfilByUsuarioId(remitenteUsuarioId);
        if (!remitente.getId().equals(request.getRemitenteEstudianteId())) {
            throw new IllegalArgumentException(
                    "El remitenteEstudianteId no coincide con el usuario autenticado");
        }

        // Verificar destinatario
        EstudianteResponse destinatario = estudianteService.getById(
                request.getDestinatarioEstudianteId());
        if (destinatario == null) {
            throw new IllegalArgumentException("El estudiante destinatario no existe");
        }

        // Solo entre compañeros del mismo grupo
        boolean mismoGrupo = remitente.getGrado().equalsIgnoreCase(destinatario.getGrado())
                && remitente.getSeccion().equalsIgnoreCase(destinatario.getSeccion());
        if (!mismoGrupo) {
            throw new IllegalArgumentException(
                    "Solo puedes enviar solicitudes a compañeros de tu mismo grado y sección");
        }

        // Obtener el Usuario del destinatario (Notificacion usa @ManyToOne Usuario)
        // NOTA: si EstudianteResponse no tiene getUsuarioId(), usa el método de tu
        // UsuarioRepository que busque por estudianteId, p.ej.:
        //   usuarioRepository.findByEstudiante_Id(destinatario.getId())
        Usuario usuarioDestinatario = usuarioRepository
                .findById(destinatario.getUsuarioId())
                .orElseThrow(() -> new IllegalArgumentException("Usuario destinatario no encontrado"));

        // Mensaje con remitenteEstudianteId codificado al final (separado por "|")
        String mensajeFinal = request.getMensaje() + "|" + request.getRemitenteEstudianteId();

        Notificacion notif = new Notificacion();
        notif.setUsuario(usuarioDestinatario);
        notif.setTipo(request.getTipo());
        notif.setTitulo(getTitulo(request.getTipo()));
        notif.setMensaje(mensajeFinal);
        notif.setLeida(false);
        // fechaCreacion la asigna @PrePersist automáticamente

        notificacionRepository.save(notif);
        log.info("📬 Notif '{}': {} → {}", request.getTipo(), remitente.getId(), destinatario.getId());
    }

    private String getTitulo(String tipo) {
        return switch (tipo) {
            case "amistad_solicitud" -> "Solicitud de amistad";
            case "amistad_aceptada"  -> "Solicitud aceptada ✅";
            case "amistad_rechazada" -> "Solicitud rechazada";
            default -> "Notificación";
        };
    }

    /**
     * Crea una solicitud de amistad (estado "pendiente"). Idempotente.
     */
    public void enviarSolicitudAmistad(UUID solicitanteId, UUID receptorId) {
        if (solicitanteId.equals(receptorId)) {
            throw new IllegalArgumentException("No puedes enviarte una solicitud a ti mismo");
        }
        Optional<Amistad> existente = amistadRepository.findEntre(solicitanteId, receptorId);
        if (existente.isPresent()) {
            return; // ya existe (pendiente o aceptada)
        }
        amistadRepository.save(new Amistad(solicitanteId, receptorId, "pendiente"));
    }

    /**
     * Acepta una solicitud pendiente (el receptor acepta).
     */
    public void aceptarSolicitud(UUID receptorId, UUID solicitanteId) {
        Amistad amistad = amistadRepository.findEntre(receptorId, solicitanteId)
                .orElseThrow(() -> new IllegalArgumentException("No existe una solicitud entre estos estudiantes"));

        if (!"pendiente".equals(amistad.getEstado())) {
            return; // ya aceptada
        }
        amistad.setEstado("aceptada");
        amistadRepository.save(amistad);
    }

    /**
     * Rechaza/elimina una solicitud pendiente o una amistad existente.
     */
    public void rechazarOEliminar(UUID miId, UUID otroId) {
        amistadRepository.eliminar(miId, otroId);
    }

    public boolean sonAmigos(UUID a, UUID b) {
        return amistadRepository.sonAmigos(a, b);
    }

    public List<UUID> getAmigosIds(UUID estudianteId) {
        return amistadRepository.findAmigosIds(estudianteId);
    }

    /**
     * Devuelve los estudiantes que me enviaron solicitud pendiente.
     */
    public List<EstudianteResponse> getSolicitudesRecibidas(UUID miId) {
        return amistadRepository.findSolicitudesRecibidas(miId).stream()
                .map(a -> estudianteService.getById(a.getSolicitanteId()))
                .collect(Collectors.toList());
    }

    /**
     * Devuelve los IDs de estudiantes a los que les envié solicitud pendiente.
     */
    public List<UUID> getSolicitudesEnviadasIds(UUID miId) {
        return amistadRepository.findSolicitudesEnviadas(miId).stream()
                .map(Amistad::getReceptorId)
                .collect(Collectors.toList());
    }

    /**
     * Estado de la relación entre yo y otro estudiante.
     * Retorna: "ninguno" | "pendiente_enviada" | "pendiente_recibida" | "amigos"
     */
    public String getEstadoRelacion(UUID miId, UUID otroId) {
        Optional<Amistad> rel = amistadRepository.findEntre(miId, otroId);
        if (rel.isEmpty()) return "ninguno";

        Amistad a = rel.get();
        if ("aceptada".equals(a.getEstado())) return "amigos";

        // pendiente
        if (a.getSolicitanteId().equals(miId)) return "pendiente_enviada";
        return "pendiente_recibida";
    }
}