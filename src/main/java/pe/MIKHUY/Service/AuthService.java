package pe.MIKHUY.Service;

import pe.MIKHUY.DTOs.request.ChangePasswordRequest;
import pe.MIKHUY.DTOs.request.LoginRequest;
import pe.MIKHUY.DTOs.request.RegisterStudentRequest;
import pe.MIKHUY.DTOs.response.AuthResponse;

/**
 * Servicio de Autenticación
 */
public interface AuthService {

    /**
     * Login de usuario
     */
    AuthResponse login(LoginRequest request);

    /**
     * Registro de estudiante
     */
    AuthResponse registerStudent(RegisterStudentRequest request);

    /**
     * Verificar token JWT
     */
    boolean verifyToken(String token);

    /**
     * Cambiar contraseña
     */
    void changePassword(String userId, ChangePasswordRequest request);

    /**
     * Refrescar token (opcional)
     */
    AuthResponse refreshToken(String token);
}