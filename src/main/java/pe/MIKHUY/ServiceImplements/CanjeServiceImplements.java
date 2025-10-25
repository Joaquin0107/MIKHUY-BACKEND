package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.request.CanjeRequest;
import pe.MIKHUY.DTOs.response.CanjeResponse;
import pe.MIKHUY.Entities.Beneficio;
import pe.MIKHUY.Entities.Canje;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Repositories.BeneficioRepository;
import pe.MIKHUY.Repositories.CanjeRepository;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Service.CanjeService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CanjeServiceImplements implements CanjeService {
    private final CanjeRepository canjeRepository;
    private final EstudianteRepository estudianteRepository;
    private final BeneficioRepository beneficioRepository;

    @Override
    @Transactional
    public CanjeResponse realizarCanje(UUID estudianteId, CanjeRequest request) {
        log.info("Realizando canje para estudiante: {}", estudianteId);

        // Verificar estudiante
        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        // Verificar beneficio
        Beneficio beneficio = beneficioRepository.findById(request.getBeneficioId())
                .orElseThrow(() -> new RuntimeException("Beneficio no encontrado"));

        // Calcular puntos necesarios
        Integer puntosNecesarios = beneficio.getPuntosRequeridos() * request.getCantidad();

        // Validaciones
        if (!beneficio.getActivo()) {
            throw new RuntimeException("El beneficio no está disponible");
        }

        if (beneficio.getStock() < request.getCantidad()) {
            throw new RuntimeException("Stock insuficiente");
        }

        if (estudiante.getPuntosAcumulados() < puntosNecesarios) {
            throw new RuntimeException("Puntos insuficientes");
        }

        // Descontar puntos del estudiante
        boolean puntosDescontados = estudiante.descontarPuntos(puntosNecesarios);
        if (!puntosDescontados) {
            throw new RuntimeException("Error al descontar puntos");
        }
        estudianteRepository.save(estudiante);

        // Descontar stock del beneficio
        beneficio.descontarStock(request.getCantidad());
        beneficioRepository.save(beneficio);

        // Crear canje
        Canje canje = new Canje();
        canje.setEstudiante(estudiante);
        canje.setBeneficio(beneficio);
        canje.setCantidad(request.getCantidad());
        canje.setPuntosGastados(puntosNecesarios);
        canje.setEstado(Canje.EstadoCanjeEnum.pendiente);
        canje.setFechaCanje(LocalDateTime.now());

        canje = canjeRepository.save(canje);

        log.info("Canje realizado exitosamente. ID: {}", canje.getId());

        return mapToResponse(canje);
    }

    @Override
    public List<CanjeResponse> getCanjesByEstudiante(UUID estudianteId) {
        log.info("Obteniendo canjes del estudiante: {}", estudianteId);

        return canjeRepository.findByEstudianteIdOrderByFechaCanjeDesc(estudianteId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<CanjeResponse> getCanjesPendientes(UUID estudianteId) {
        log.info("Obteniendo canjes pendientes del estudiante: {}", estudianteId);

        return canjeRepository.findByEstudianteIdAndEstadoOrderByFechaCanjeDesc(
                        estudianteId,
                        Canje.EstadoCanjeEnum.pendiente
                ).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CanjeResponse marcarComoEntregado(UUID canjeId) {
        log.info("Marcando canje como entregado: {}", canjeId);

        Canje canje = canjeRepository.findById(canjeId)
                .orElseThrow(() -> new RuntimeException("Canje no encontrado"));

        if (canje.getEstado() != Canje.EstadoCanjeEnum.pendiente) {
            throw new RuntimeException("El canje no está en estado pendiente");
        }

        canje.setEstado(Canje.EstadoCanjeEnum.entregado);
        canje.setFechaEntrega(LocalDateTime.now());

        canje = canjeRepository.save(canje);

        log.info("Canje marcado como entregado");

        return mapToResponse(canje);
    }

    @Override
    @Transactional
    public CanjeResponse cancelarCanje(UUID canjeId, UUID estudianteId) {
        log.info("Cancelando canje: {}", canjeId);

        Canje canje = canjeRepository.findById(canjeId)
                .orElseThrow(() -> new RuntimeException("Canje no encontrado"));

        // Verificar que el canje pertenece al estudiante
        if (!canje.getEstudiante().getId().equals(estudianteId)) {
            throw new RuntimeException("No tienes permisos para cancelar este canje");
        }

        // Solo se pueden cancelar canjes pendientes
        if (canje.getEstado() != Canje.EstadoCanjeEnum.pendiente) {
            throw new RuntimeException("Solo se pueden cancelar canjes pendientes");
        }

        // Devolver puntos al estudiante
        Estudiante estudiante = canje.getEstudiante();
        estudiante.agregarPuntos(canje.getPuntosGastados());
        estudianteRepository.save(estudiante);

        // Devolver stock al beneficio
        Beneficio beneficio = canje.getBeneficio();
        beneficio.setStock(beneficio.getStock() + canje.getCantidad());
        beneficioRepository.save(beneficio);

        // Marcar canje como cancelado
        canje.setEstado(Canje.EstadoCanjeEnum.cancelado);
        canje = canjeRepository.save(canje);

        log.info("Canje cancelado. Puntos devueltos: {}", canje.getPuntosGastados());

        return mapToResponse(canje);
    }

    /**
     * Mapear entidad a DTO Response
     */
    private CanjeResponse mapToResponse(Canje canje) {
        Estudiante estudiante = canje.getEstudiante();
        Beneficio beneficio = canje.getBeneficio();

        boolean puedeSerCancelado = canje.getEstado() == Canje.EstadoCanjeEnum.pendiente;
        String tiempoTranscurrido = calcularTiempoTranscurrido(canje.getFechaCanje());

        return CanjeResponse.builder()
                .id(canje.getId())
                .estudianteId(estudiante.getId())
                .estudianteNombre(estudiante.getUsuario().getNombreCompleto())
                .estudianteGrado(estudiante.getGrado())
                .estudianteSeccion(estudiante.getSeccion())
                .beneficioId(beneficio.getId())
                .beneficioNombre(beneficio.getNombre())
                .beneficioDescripcion(beneficio.getDescripcion())
                .beneficioCategoria(beneficio.getCategoria())
                .beneficioImagenUrl(beneficio.getImagenUrl())
                .beneficioPuntosRequeridos(beneficio.getPuntosRequeridos())
                .cantidad(canje.getCantidad())
                .puntosGastados(canje.getPuntosGastados())
                .estado(canje.getEstado().name())
                .fechaCanje(canje.getFechaCanje())
                .fechaEntrega(canje.getFechaEntrega())
                .puedeSerCancelado(puedeSerCancelado)
                .tiempoTranscurrido(tiempoTranscurrido)
                .build();
    }

    /**
     * Calcular tiempo transcurrido desde una fecha
     */
    private String calcularTiempoTranscurrido(LocalDateTime fecha) {
        LocalDateTime ahora = LocalDateTime.now();
        long segundos = java.time.Duration.between(fecha, ahora).getSeconds();

        if (segundos < 60) {
            return "Hace " + segundos + " segundos";
        } else if (segundos < 3600) {
            long minutos = segundos / 60;
            return "Hace " + minutos + (minutos == 1 ? " minuto" : " minutos");
        } else if (segundos < 86400) {
            long horas = segundos / 3600;
            return "Hace " + horas + (horas == 1 ? " hora" : " horas");
        } else {
            long dias = segundos / 86400;
            return "Hace " + dias + (dias == 1 ? " día" : " días");
        }
    }
}