package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "usuarios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", unique = true, nullable = false, length = 255)
    private String email;

    @Column(name = "password_hash", nullable = false, length = 255)
    private String passwordHash;

    @Enumerated(EnumType.STRING)
    @Column(name = "rol", nullable = false, length = 20)
    private RolEnum rol;

    @Column(name = "nombres", nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellidos", nullable = false, length = 100)
    private String apellidos;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @Column(name = "ultima_conexion")
    private LocalDateTime ultimaConexion;

    // ── Verificación de cuenta ──────────────────────────────────────────────
    @Column(name = "verificado", nullable = false)
    private Boolean verificado = false;

    @Column(name = "token_verificacion", length = 255)
    private String tokenVerificacion;

    @Column(name = "token_expira")
    private LocalDateTime tokenExpira;

    // Relaciones
    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Estudiante estudiante;

    @OneToOne(mappedBy = "usuario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Profesor profesor;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (verificado == null) {
            verificado = false;
        }
    }

    // Enum para roles
    public enum RolEnum {
        student,
        teacher,
        admin
    }

    // Método auxiliar
    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    // Helpers de verificación
    public boolean isVerificado() {
        return Boolean.TRUE.equals(verificado);
    }

    public boolean tokenEstaVigente() {
        return tokenExpira != null && LocalDateTime.now().isBefore(tokenExpira);
    }
}