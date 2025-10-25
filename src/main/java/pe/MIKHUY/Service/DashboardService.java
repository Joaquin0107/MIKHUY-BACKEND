package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.response.DashboardEstudianteResponse;

import java.util.UUID;

/**
 * Servicio de Dashboard
 * Consolida toda la información del estudiante en una sola respuesta
 */
public interface DashboardService {

    /**
     * Obtener dashboard completo del estudiante
     * Incluye: perfil, estadísticas, juegos, sesiones, notificaciones, beneficios, ranking
     */
    DashboardEstudianteResponse getDashboardEstudiante(UUID estudianteId);
}