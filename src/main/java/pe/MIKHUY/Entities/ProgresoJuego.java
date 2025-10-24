package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "progreso_juegos",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "juego_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgresoJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "juego_id", nullable = false)
    private Juego juego;

    @Column(name = "nivel_actual")
    private Integer nivelActual = 1;

    @Column(name = "puntos_ganados")
    private Integer puntosGanados = 0;

    @Column(name = "veces_jugado")
    private Integer vecesJugado = 0;

    @Column(name = "ultima_jugada")
    private LocalDateTime ultimaJugada;

    @Column(name = "completado")
    private Boolean completado = false;

    @Column(name = "fecha_inicio")
    private LocalDateTime fechaInicio;

    // Relaciones
    @OneToMany(mappedBy = "progreso", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SesionJuego> sesiones;

    @PrePersist
    protected void onCreate() {
        if (fechaInicio == null) {
            fechaInicio = LocalDateTime.now();
        }
        if (nivelActual == null) {
            nivelActual = 1;
        }
        if (puntosGanados == null) {
            puntosGanados = 0;
        }
        if (vecesJugado == null) {
            vecesJugado = 0;
        }
        if (completado == null) {
            completado = false;
        }
    }

    // Métodos auxiliares
    public void incrementarNivel() {
        this.nivelActual++;
    }

    public void agregarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0) {
            this.puntosGanados = (this.puntosGanados == null ? 0 : this.puntosGanados) + puntos;
        }
    }

    public void incrementarVecesJugado() {
        this.vecesJugado = (this.vecesJugado == null ? 0 : this.vecesJugado) + 1;
        this.ultimaJugada = LocalDateTime.now();
    }

    public Double calcularPorcentajeCompletado() {
        if (juego != null && juego.getMaxNiveles() != null && juego.getMaxNiveles() > 0) {
            return (nivelActual.doubleValue() / juego.getMaxNiveles()) * 100;
        }
        return 0.0;
    }
}