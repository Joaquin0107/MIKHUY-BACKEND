package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "correos_enviados")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CorreoEnviado {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporte_id")
    private Reporte reporte;

    @Column(name = "destinatario_email", nullable = false, length = 255)
    private String destinatarioEmail;

    @Column(name = "asunto", nullable = false, length = 500)
    private String asunto;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    @Column(name = "adjunto_url", length = 500)
    private String adjuntoUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoCorreoEnum estado = EstadoCorreoEnum.enviado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @PrePersist
    protected void onCreate() {
        if (fechaEnvio == null) {
            fechaEnvio = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoCorreoEnum.enviado;
        }
    }

    // Enum para estado
    public enum EstadoCorreoEnum {
        enviado,
        fallido,
        pendiente
    }
}