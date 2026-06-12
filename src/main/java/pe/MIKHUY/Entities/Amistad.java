package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "amistades", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"solicitante_id", "receptor_id"})
})
@Data
@NoArgsConstructor
public class Amistad {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "solicitante_id", nullable = false)
    private UUID solicitanteId; // quien envió la solicitud

    @Column(name = "receptor_id", nullable = false)
    private UUID receptorId; // quien la recibe

    @Column(nullable = false)
    private String estado; // "pendiente" | "aceptada"

    @Column(nullable = false)
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDateTime.now();
    }

    public Amistad(UUID solicitanteId, UUID receptorId, String estado) {
        this.solicitanteId = solicitanteId;
        this.receptorId = receptorId;
        this.estado = estado;
    }
}