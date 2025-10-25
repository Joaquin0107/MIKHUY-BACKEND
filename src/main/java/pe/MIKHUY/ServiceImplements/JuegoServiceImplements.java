package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.Service.JuegoService;
import pe.MIKHUY.DTOs.response.JuegoResponse;
import pe.MIKHUY.Entities.Juego;
import pe.MIKHUY.Entities.ProgresoJuego;
import pe.MIKHUY.Repositories.JuegoRepository;
import pe.MIKHUY.Repositories.ProgresoJuegoRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class JuegoServiceImplements implements JuegoService {
    private final JuegoRepository juegoRepository;
    private final ProgresoJuegoRepository progresoJuegoRepository;

    @Override
    public List<JuegoResponse> getAllActive() {
        log.info("Obteniendo todos los juegos activos");

        return juegoRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<JuegoResponse> getJuegosConProgreso(UUID estudianteId) {
        log.info("Obteniendo juegos con progreso del estudiante: {}", estudianteId);

        List<Juego> juegosActivos = juegoRepository.findByActivoTrue();

        return juegosActivos.stream()
                .map(juego -> {
                    JuegoResponse response = mapToResponse(juego);

                    // Buscar progreso del estudiante en este juego
                    Optional<ProgresoJuego> progresoOpt = progresoJuegoRepository
                            .findByEstudianteIdAndJuegoId(estudianteId, juego.getId());

                    if (progresoOpt.isPresent()) {
                        ProgresoJuego progreso = progresoOpt.get();
                        response.setProgresoId(progreso.getId());
                        response.setNivelActual(progreso.getNivelActual());
                        response.setPuntosGanados(progreso.getPuntosGanados());
                        response.setVecesJugado(progreso.getVecesJugado());
                        response.setUltimaJugada(progreso.getUltimaJugada());
                        response.setCompletado(progreso.getCompletado());
                        response.setPorcentajeCompletado(progreso.calcularPorcentajeCompletado());
                    } else {
                        // No tiene progreso en este juego
                        response.setNivelActual(0);
                        response.setPuntosGanados(0);
                        response.setVecesJugado(0);
                        response.setCompletado(false);
                        response.setPorcentajeCompletado(0.0);
                    }

                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public JuegoResponse getById(UUID juegoId) {
        log.info("Obteniendo juego por ID: {}", juegoId);

        Juego juego = juegoRepository.findById(juegoId)
                .orElseThrow(() -> new RuntimeException("Juego no encontrado"));

        return mapToResponse(juego);
    }

    @Override
    public List<JuegoResponse> getByCategoria(String categoria) {
        log.info("Obteniendo juegos por categoría: {}", categoria);

        return juegoRepository.findByCategoriaAndActivoTrue(categoria).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    /**
     * Mapear entidad a DTO Response
     */
    private JuegoResponse mapToResponse(Juego juego) {
        return JuegoResponse.builder()
                .id(juego.getId())
                .nombre(juego.getNombre())
                .descripcion(juego.getDescripcion())
                .categoria(juego.getCategoria())
                .maxNiveles(juego.getMaxNiveles())
                .puntosPorNivel(juego.getPuntosPorNivel())
                .puntosMaximos(juego.calcularPuntosMaximos())
                .activo(juego.getActivo())
                .fechaCreacion(juego.getFechaCreacion())
                .build();
    }
}