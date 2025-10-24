package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "sesiones_juego")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SesionJuego {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "progreso_id", nullable = false)
    private ProgresoJuego progreso;

    @Column(name = "nivel_jugado", nullable = false)
    private Integer nivelJugado;

    @Column(name = "puntos_obtenidos")
    private Integer puntosObtenidos = 0;

    @Column(name = "tiempo_jugado")
    private Integer tiempoJugado; // en segundos

    @Column(name = "completado")
    private Boolean completado = false;

    @Column(name = "fecha_sesion")
    private LocalDateTime fechaSesion;

    // Relaciones con respuestas de juegos específicos
    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<NutrimentalRespuesta> nutrimentalRespuestas;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Reto7DiasRegistro> reto7DiasRegistros;

    @OneToMany(mappedBy = "sesion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CoachRespuesta> coachRespuestas;

    @PrePersist
    protected void onCreate() {
        if (fechaSesion == null) {
            fechaSesion = LocalDateTime.now();
        }
        if (puntosObtenidos == null) {
            puntosObtenidos = 0;
        }
        if (completado == null) {
            completado = false;
        }
    }
}