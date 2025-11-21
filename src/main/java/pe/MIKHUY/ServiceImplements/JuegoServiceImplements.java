package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.DTOs.response.RankingResponse;
import pe.MIKHUY.Service.JuegoService;
import pe.MIKHUY.DTOs.response.JuegoResponse;
import pe.MIKHUY.Entities.Juego;
import pe.MIKHUY.Entities.ProgresoJuego;
import pe.MIKHUY.Repositories.JuegoRepository;
import pe.MIKHUY.Repositories.ProgresoJuegoRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.function.Function;


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
        List<Juego> juegosActivos = juegoRepository.findByActivoTrue();
        List<ProgresoJuego> progresos = progresoJuegoRepository.findByEstudianteId(estudianteId);

        Map<UUID, ProgresoJuego> progresoMap = progresos.stream()
                .collect(Collectors.toMap(p -> p.getJuego().getId(), Function.identity()));

        return juegosActivos.stream().map(juego -> {
            ProgresoJuego progreso = progresoMap.get(juego.getId());
            return JuegoResponse.builder()
                    .id(juego.getId())
                    .nombre(juego.getNombre())
                    .descripcion(juego.getDescripcion())
                    .categoria(juego.getCategoria())
                    .imagen(juego.getImagen())
                    .maxNiveles(juego.getMaxNiveles())
                    .puntosPorNivel(juego.getPuntosPorNivel())
                    .puntosMaximos(juego.getMaxNiveles() * juego.getPuntosPorNivel())
                    .activo(juego.getActivo())
                    .fechaCreacion(LocalDateTime.parse(juego.getFechaCreacion().toString()))
                    .progresoId(progreso != null ? UUID.fromString(progreso.getId().toString()) : null)
                    .nivelActual(progreso != null ? progreso.getNivelActual() : 0)
                    .puntosGanados(progreso != null ? progreso.getPuntosGanados() : 0)
                    .vecesJugado(progreso != null ? progreso.getVecesJugado() : 0)
                    .ultimaJugada(progreso != null && progreso.getUltimaJugada() != null ? LocalDateTime.parse(progreso.getUltimaJugada().toString()) : null)
                    .completado(progreso != null ? progreso.getCompletado() : false)
                    .porcentajeCompletado(progreso != null ? progreso.calcularPorcentajeCompletado() : 0.0)
                    .build();
        }).toList();
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

    @Override
    public RankingResponse getRankingPorJuego(UUID juegoId, UUID estudianteId) {
        return null;
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
                .imagen(juego.getImagen())
                .maxNiveles(juego.getMaxNiveles())
                .puntosPorNivel(juego.getPuntosPorNivel())
                .puntosMaximos(juego.calcularPuntosMaximos())
                .activo(juego.getActivo())
                .fechaCreacion(juego.getFechaCreacion())
                .build();
    }
}