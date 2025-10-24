package pe.MIKHUY.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

@Entity
@Table(name = "reportes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Reporte {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "estudiante_id", nullable = false)
    private Estudiante estudiante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "profesor_id")
    private Profesor profesor;

    @Column(name = "tipo_reporte", nullable = false, length = 50)
    private String tipoReporte; // 'mensual', 'trimestral', 'personalizado'

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "contenido_json", columnDefinition = "jsonb")
    private Map<String, Object> contenidoJson;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "generado_por")
    private Usuario generadoPor;

    @Column(name = "fecha_generacion")
    private LocalDateTime fechaGeneracion;

    @PrePersist
    protected void onCreate() {
        if (fechaGeneracion == null) {
            fechaGeneracion = LocalDateTime.now();
        }
    }
}