package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "beneficios")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Beneficio {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "puntos_requeridos", nullable = false)
    private Integer puntosRequeridos;

    @Column(name = "categoria", length = 50)
    private String categoria;

    @Column(name = "stock")
    private Integer stock = 0;

    @Column(name = "imagen_url", length = 500)
    private String imagenUrl;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relaciones
    @OneToMany(mappedBy = "beneficio", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Canje> canjes;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (stock == null) {
            stock = 0;
        }
    }

    // Métodos auxiliares
    public boolean hayStock(Integer cantidad) {
        return stock != null && stock >= cantidad;
    }

    public void descontarStock(Integer cantidad) {
        if (hayStock(cantidad)) {
            this.stock -= cantidad;
        }
    }
}