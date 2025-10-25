package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.request.*;
import pe.MIKHUY.DTOs.response.SesionJuegoResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Sesiones de Juego
 */
public interface SesionJuegoService {

    /**
     * Iniciar una nueva sesión de juego
     */
    SesionJuegoResponse iniciarSesion(UUID estudianteId, IniciarSesionJuegoRequest request);

    /**
     * Finalizar sesión de juego
     */
    SesionJuegoResponse finalizarSesion(FinalizarSesionJuegoRequest request);

    /**
     * Guardar respuesta de Desafío Nutrimental
     */
    void guardarRespuestaNutrimental(GuardarNutrimentalRespuestaRequest request);

    /**
     * Guardar registro de Reto 7 Días
     */
    void guardarRegistroReto7Dias(GuardarReto7DiasRegistroRequest request);

    /**
     * Guardar respuesta de Coach Exprés
     */
    void guardarRespuestaCoach(GuardarCoachRespuestaRequest request);

    /**
     * Obtener sesiones de un estudiante
     */
    List<SesionJuegoResponse> getSesionesByEstudiante(UUID estudianteId);

    /**
     * Obtener última sesión de un progreso
     */
    SesionJuegoResponse getUltimaSesion(UUID progresoId);
}