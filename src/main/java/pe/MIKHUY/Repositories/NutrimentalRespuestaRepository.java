package pe.MIKHUY.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import pe.MIKHUY.Entities.NutrimentalRespuesta;

import java.util.List;
import java.util.UUID;

@Repository
public interface NutrimentalRespuestaRepository extends JpaRepository<NutrimentalRespuesta, UUID> {

    // Buscar por sesión
    List<NutrimentalRespuesta> findBySesionId(UUID sesionId);

    // Buscar respuestas correctas de una sesión
    List<NutrimentalRespuesta> findBySesionIdAndRespuestaCorrectaTrue(UUID sesionId);

    // Contar respuestas correctas de una sesión
    long countBySesionIdAndRespuestaCorrectaTrue(UUID sesionId);

    // Contar total de respuestas de una sesión
    long countBySesionId(UUID sesionId);

    // Porcentaje de aciertos por estudiante
    @Query("SELECT nr.preguntaTema, " +
            "SUM(CASE WHEN nr.respuestaCorrecta = true THEN 1 ELSE 0 END) * 100.0 / COUNT(*) " +
            "FROM NutrimentalRespuesta nr " +
            "WHERE nr.sesion.progreso.estudiante.id = :estudianteId " +
            "GROUP BY nr.preguntaTema")
    List<Object[]> getPorcentajeAciertosPorTema(@Param("estudianteId") UUID estudianteId);

    // Temas con más errores por estudiante
    @Query("SELECT nr.preguntaTema, COUNT(*) " +
            "FROM NutrimentalRespuesta nr " +
            "WHERE nr.sesion.progreso.estudiante.id = :estudianteId " +
            "AND nr.respuestaCorrecta = false " +
            "GROUP BY nr.preguntaTema " +
            "ORDER BY COUNT(*) DESC")
    List<Object[]> getTemasConMasErrores(@Param("estudianteId") UUID estudianteId);

    // Tiempo promedio de respuesta por estudiante
    @Query("SELECT AVG(nr.tiempoRespuesta) FROM NutrimentalRespuesta nr " +
            "WHERE nr.sesion.progreso.estudiante.id = :estudianteId")
    Double getTiempoPromedioRespuesta(@Param("estudianteId") UUID estudianteId);
}
