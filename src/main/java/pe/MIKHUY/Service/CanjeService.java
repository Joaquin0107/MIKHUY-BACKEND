package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.request.CanjeRequest;
import pe.MIKHUY.DTOs.response.CanjeResponse;

import java.util.List;
import java.util.UUID;

/**
 * Servicio de Canjes
 */
public interface CanjeService {

    /**
     * Realizar un canje
     */
    CanjeResponse realizarCanje(UUID estudianteId, CanjeRequest request);

    /**
     * Obtener canjes de un estudiante
     */
    List<CanjeResponse> getCanjesByEstudiante(UUID estudianteId);

    /**
     * Obtener canjes pendientes de un estudiante
     */
    List<CanjeResponse> getCanjesPendientes(UUID estudianteId);

    /**
     * Marcar canje como entregado
     */
    CanjeResponse marcarComoEntregado(UUID canjeId);

    /**
     * Cancelar canje
     */
    CanjeResponse cancelarCanje(UUID canjeId, UUID estudianteId);
}