package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "analisis_nutricional",
        uniqueConstraints = @UniqueConstraint(columnNames = {"estudiante_id", "fecha_analisis"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AnalisisNutricional {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @Column(name = "fecha_analisis", nullable = false)
    private LocalDate fechaAnalisis;

    // Macronutrientes (%)
    @Column(name = "proteinas_porcentaje", precision = 5, scale = 2)
    private BigDecimal proteinasPorcentaje;

    @Column(name = "carbohidratos_porcentaje", precision = 5, scale = 2)
    private BigDecimal carbohidratosPorcentaje;

    @Column(name = "grasas_porcentaje", precision = 5, scale = 2)
    private BigDecimal grasasPorcentaje;

    // Vitaminas y minerales (%)
    @Column(name = "vitamina_a", precision = 5, scale = 2)
    private BigDecimal vitaminaA;

    @Column(name = "vitamina_c", precision = 5, scale = 2)
    private BigDecimal vitaminaC;

    @Column(name = "calcio", precision = 5, scale = 2)
    private BigDecimal calcio;

    @Column(name = "hierro", precision = 5, scale = 2)
    private BigDecimal hierro;

    // Datos psicológicos
    @Column(name = "etapa_cambio", length = 50)
    private String etapaCambio;

    @Column(name = "puntaje_motivacion", precision = 3, scale = 2)
    private BigDecimal puntajeMotivacion;

    @Column(name = "disposicion_cambio")
    private Integer disposicionCambio; // 1-5

    // Conocimiento nutricional
    @Column(name = "porcentaje_aciertos", precision = 5, scale = 2)
    private BigDecimal porcentajeAciertos;

    @Column(name = "temas_debiles", columnDefinition = "text[]")
    private String[] temasDebiles;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion;

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) {
            fechaCreacion = LocalDateTime.now();
        }
        if (fechaAnalisis == null) {
            fechaAnalisis = LocalDate.now();
        }
    }
}