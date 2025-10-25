package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.response.JuegoResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Juegos
 */
public interface JuegoService {

    /**
     * Obtener todos los juegos activos
     */
    List<JuegoResponse> getAllActive();

    /**
     * Obtener juegos con progreso del estudiante
     */
    List<JuegoResponse> getJuegosConProgreso(UUID estudianteId);

    /**
     * Obtener juego por ID
     */
    JuegoResponse getById(UUID juegoId);

    /**
     * Obtener juegos por categoría
     */
    List<JuegoResponse> getByCategoria(String categoria);
}