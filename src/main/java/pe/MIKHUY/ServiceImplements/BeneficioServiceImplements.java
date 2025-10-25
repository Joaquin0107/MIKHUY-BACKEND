package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import pe.MIKHUY.DTOs.response.BeneficioResponse;
import pe.MIKHUY.Entities.Beneficio;
import pe.MIKHUY.Entities.Estudiante;
import pe.MIKHUY.Repositories.BeneficioRepository;
import pe.MIKHUY.Repositories.CanjeRepository;
import pe.MIKHUY.Repositories.EstudianteRepository;
import pe.MIKHUY.Service.BeneficioService;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeneficioServiceImplements implements BeneficioService {

    private final BeneficioRepository beneficioRepository;
    private final EstudianteRepository estudianteRepository;
    private final CanjeRepository canjeRepository;

    @Override
    public List<BeneficioResponse> getAllActive() {
        log.info("Obteniendo todos los beneficios activos");

        return beneficioRepository.findByActivoTrue().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public List<BeneficioResponse> getDisponiblesParaEstudiante(UUID estudianteId) {
        log.info("Obteniendo beneficios disponibles para estudiante: {}", estudianteId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Integer puntosEstudiante = estudiante.getPuntosAcumulados();

        return beneficioRepository.findByActivoTrueAndStockGreaterThan(0).stream()
                .map(beneficio -> {
                    BeneficioResponse response = mapToResponse(beneficio);
                    response.setPuedeCanjearse(puntosEstudiante >= beneficio.getPuntosRequeridos());
                    return response;
                })
                .collect(Collectors.toList());
    }

    @Override
    public BeneficioResponse getById(UUID beneficioId) {
        log.info("Obteniendo beneficio por ID: {}", beneficioId);

        Beneficio beneficio = beneficioRepository.findById(beneficioId)
                .orElseThrow(() -> new RuntimeException("Beneficio no encontrado"));

        return mapToResponse(beneficio);
    }

    @Override
    public List<BeneficioResponse> getByCategoria(String categoria) {
        log.info("Obteniendo beneficios por categoría: {}", categoria);

        return beneficioRepository.findByCategoriaAndActivoTrue(categoria).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    public boolean puedeCanjenar(UUID estudianteId, UUID beneficioId, Integer cantidad) {
        log.info("Verificando si estudiante {} puede canjear beneficio {}", estudianteId, beneficioId);

        Estudiante estudiante = estudianteRepository.findById(estudianteId)
                .orElseThrow(() -> new RuntimeException("Estudiante no encontrado"));

        Beneficio beneficio = beneficioRepository.findById(beneficioId)
                .orElseThrow(() -> new RuntimeException("Beneficio no encontrado"));

        // Verificar puntos
        Integer puntosNecesarios = beneficio.getPuntosRequeridos() * cantidad;
        if (estudiante.getPuntosAcumulados() < puntosNecesarios) {
            log.warn("Puntos insuficientes");
            return false;
        }

        // Verificar stock
        if (beneficio.getStock() < cantidad) {
            log.warn("Stock insuficiente");
            return false;
        }

        // Verificar que esté activo
        if (!beneficio.getActivo()) {
            log.warn("Beneficio inactivo");
            return false;
        }

        return true;
    }

    /**
     * Mapear entidad a DTO Response
     */
    private BeneficioResponse mapToResponse(Beneficio beneficio) {
        long vecesCanjeado = canjeRepository.countByBeneficioId(beneficio.getId());

        return BeneficioResponse.builder()
                .id(beneficio.getId())
                .nombre(beneficio.getNombre())
                .descripcion(beneficio.getDescripcion())
                .puntosRequeridos(beneficio.getPuntosRequeridos())
                .categoria(beneficio.getCategoria())
                .stock(beneficio.getStock())
                .disponible(beneficio.getStock() > 0)
                .imagenUrl(beneficio.getImagenUrl())
                .activo(beneficio.getActivo())
                .fechaCreacion(beneficio.getFechaCreacion())
                .vecesCanjeado((int) vecesCanjeado)
                .build();
    }
}