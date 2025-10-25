package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.response.BeneficioResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Beneficios
 */
public interface BeneficioService {

    /**
     * Obtener todos los beneficios activos
     */
    List<BeneficioResponse> getAllActive();

    /**
     * Obtener beneficios disponibles para un estudiante (con stock y puntos suficientes)
     */
    List<BeneficioResponse> getDisponiblesParaEstudiante(UUID estudianteId);

    /**
     * Obtener beneficio por ID
     */
    BeneficioResponse getById(UUID beneficioId);

    /**
     * Obtener beneficios por categoría
     */
    List<BeneficioResponse> getByCategoria(String categoria);

    /**
     * Verificar si un estudiante puede canjear un beneficio
     */
    boolean puedeCanjenar(UUID estudianteId, UUID beneficioId, Integer cantidad);
}