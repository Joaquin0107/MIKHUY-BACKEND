package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "estudiantes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    @Column(name = "edad", nullable = false)
    private Integer edad;

    @Column(name = "grado", nullable = false, length = 10)
    private String grado;

    @Column(name = "seccion", nullable = false, length = 5)
    private String seccion;

    @Column(name = "talla", precision = 5, scale = 2)
    private BigDecimal talla; // en metros

    @Column(name = "peso", precision = 5, scale = 2)
    private BigDecimal peso; // en kilogramos

    @Column(name = "puntos_acumulados")
    private Integer puntosAcumulados = 0;

    @Column(name = "fecha_registro")
    private LocalDateTime fechaRegistro;

    // Relaciones
    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ProgresoJuego> progresoJuegos;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AnalisisNutricional> analisisNutricionales;

    @OneToMany(mappedBy = "estudiante", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Canje> canjes;

    @PrePersist
    protected void onCreate() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
        if (puntosAcumulados == null) {
            puntosAcumulados = 0;
        }
    }

    // Métodos auxiliares
    public BigDecimal calcularIMC() {
        if (peso != null && talla != null && talla.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal tallaCuadrado = talla.multiply(talla);
            return peso.divide(tallaCuadrado, 2, BigDecimal.ROUND_HALF_UP);
        }
        return null;
    }

    public void agregarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0) {
            this.puntosAcumulados = (this.puntosAcumulados == null ? 0 : this.puntosAcumulados) + puntos;
        }
    }

    public boolean descontarPuntos(Integer puntos) {
        if (puntos != null && puntos > 0 && this.puntosAcumulados != null && this.puntosAcumulados >= puntos) {
            this.puntosAcumulados -= puntos;
            return true;
        }
        return false;
    }
}