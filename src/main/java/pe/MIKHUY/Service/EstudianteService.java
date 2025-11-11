package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.request.UpdateProfileRequest;
import pe.MIKHUY.DTOs.response.EstadisticasEstudianteResponse;
import pe.MIKHUY.DTOs.response.EstudianteResponse;
import pe.MIKHUY.DTOs.response.RankingResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Estudiantes
 */
public interface EstudianteService {

    /**
     * Obtener perfil del estudiante por ID de usuario
     */
    EstudianteResponse getPerfilByUsuarioId(UUID usuarioId);

    /**
     * Obtener estudiante por ID
     */
    EstudianteResponse getById(UUID id);

    /**
     * Obtener todos los estudiantes
     */
    List<EstudianteResponse> getAll();

    /**
     * Obtener estudiantes por grado
     */
    List<EstudianteResponse> getByGrado(String grado);

    /**
     * Obtener estudiantes por grado y sección
     */
    List<EstudianteResponse> getByGradoAndSeccion(String grado, String seccion);

    /**
     * Actualizar perfil del estudiante
     */
    EstudianteResponse updatePerfil(UUID usuarioId, UpdateProfileRequest request);

    /**
     * Obtener estadísticas del estudiante
     */
    EstadisticasEstudianteResponse getEstadisticas(UUID estudianteId);

    /**
     * Obtener ranking de estudiantes
     */
    RankingResponse getRanking(UUID estudianteIdActual);

    /**
     * Agregar puntos a un estudiante
     */
    void agregarPuntos(UUID estudianteId, Integer puntos);

    /**
     * Obtener puntos acumulados de un estudiante
     */
    Integer getPuntos(UUID estudianteId);

    /**
     * Descontar puntos de un estudiante
     */
    boolean descontarPuntos(UUID estudianteId, Integer puntos);
}