package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "juegos")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Juego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "categoria", nullable = false, length = 50)
    private String categoria;

    @Column(name = "imagen", length = 255)
    private String imagen;

    @Column(name = "max_niveles")
    private Integer maxNiveles = 10;

    @Column(name = "puntos_por_nivel")
    private Integer puntosPorNivel = 50;

    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    // Relaciones
    @OneToMany(mappedBy = "juego", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgresoJuego> progresoJuegos;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (activo == null) {
            activo = true;
        }
        if (maxNiveles == null) {
            maxNiveles = 10;
        }
        if (puntosPorNivel == null) {
            puntosPorNivel = 50;
        }
    }

    // Métodos auxiliares
    public Integer calcularPuntosMaximos() {
        return maxNiveles * puntosPorNivel;
    }
}