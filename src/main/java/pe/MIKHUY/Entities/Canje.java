package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "canjes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Canje {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "beneficio_id", nullable = false)
    private Beneficio beneficio;

    @Column(name = "cantidad")
    private Integer cantidad = 1;

    @Column(name = "puntos_gastados", nullable = false)
    private Integer puntosGastados;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", length = 20)
    private EstadoCanjeEnum estado = EstadoCanjeEnum.pendiente;

    @Column(name = "fecha_canje")
    private LocalDateTime fechaCanje;

    @Column(name = "fecha_entrega")
    private LocalDateTime fechaEntrega;

    @PrePersist
    protected void onCreate() {
        if (fechaCanje == null) {
            fechaCanje = LocalDateTime.now();
        }
        if (estado == null) {
            estado = EstadoCanjeEnum.pendiente;
        }
        if (cantidad == null) {
            cantidad = 1;
        }
    }

    // Enum para estado
    public enum EstadoCanjeEnum {
        pendiente,
        entregado,
        cancelado
    }
}