package pe.MIKHUY.ServiceImplements;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.MIKHUY.DTOs.request.CrearGrupoRequest;
import pe.MIKHUY.DTOs.response.*;
import pe.MIKHUY.Entities.*;
import pe.MIKHUY.Repositories.*;
import pe.MIKHUY.Service.GrupoService;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GrupoServiceImpl implements GrupoService {

    private final GrupoEstudioRepository grupoRepository;
    private final ProfesorRepository profesorRepository;
    private final EstudianteRepository estudianteRepository;
    private final ProgresoJuegoRepository progresoJuegoRepository;
    private final SesionJuegoRepository sesionJuegoRepository;

    @Override
    @Transactional
    public GrupoResumenResponse crearGrupo(UUID profesorUsuarioId, CrearGrupoRequest request) {
        if (request.getEstudianteIds().size() > 5)
            throw new IllegalArgumentException("Un grupo puede tener máximo 5 estudiantes");

        Profesor profesor = profesorRepository.findByUsuarioId(profesorUsuarioId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));

        List<Estudiante> estudiantes = estudianteRepository.findAllById(request.getEstudianteIds());

        GrupoEstudio grupo = new GrupoEstudio();
        grupo.setNombre(request.getNombre());
        grupo.setProfesor(profesor);
        grupo.setEstudiantes(estudiantes);

        grupo = grupoRepository.save(grupo);
        return buildResumen(grupo);
    }

    @Override
    public List<GrupoResumenResponse> getGruposDelProfesor(UUID profesorUsuarioId) {
        return grupoRepository.findByProfesorUsuarioId(profesorUsuarioId)
                .stream().map(this::buildResumen).collect(Collectors.toList());
    }

    @Override
    public GrupoResumenResponse getGrupoDetalle(UUID grupoId) {
        GrupoEstudio grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));
        return buildResumen(grupo);
    }

    @Override
    @Transactional
    public GrupoResumenResponse actualizarGrupo(UUID grupoId, CrearGrupoRequest request) {
        if (request.getEstudianteIds().size() > 5)
            throw new IllegalArgumentException("Un grupo puede tener máximo 5 estudiantes");

        GrupoEstudio grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        grupo.setNombre(request.getNombre());
        grupo.setEstudiantes(estudianteRepository.findAllById(request.getEstudianteIds()));
        grupo = grupoRepository.save(grupo);
        return buildResumen(grupo);
    }

    @Override
    @Transactional
    public void eliminarGrupo(UUID grupoId) {
        grupoRepository.deleteById(grupoId);
    }

    private GrupoResumenResponse buildResumen(GrupoEstudio grupo) {
        List<Estudiante> miembros = grupo.getEstudiantes();

        // Miembros con métricas individuales
        List<MiembroGrupoResponse> miembrosDto = new ArrayList<>();
        for (int i = 0; i < miembros.size(); i++) {
            Estudiante e = miembros.get(i);
            List<SesionJuego> sesiones = sesionJuegoRepository.findByEstudianteId(e.getId());
            long completados = sesiones.stream().filter(SesionJuego::getCompletado).count();
            miembrosDto.add(MiembroGrupoResponse.builder()
                    .id(e.getId())
                    .nombre(e.getUsuario().getNombres())
                    .apellido(e.getUsuario().getApellidos())
                    .puntosAcumulados(e.getPuntosAcumulados() != null ? e.getPuntosAcumulados() : 0)
                    .totalSesiones(sesiones.size())
                    .juegosCompletados((int) completados)
                    .build());
        }

        // Ordenar por puntos para ranking interno
        miembrosDto.sort(Comparator.comparingInt(MiembroGrupoResponse::getPuntosAcumulados).reversed());
        for (int i = 0; i < miembrosDto.size(); i++) miembrosDto.get(i).setPosicionEnGrupo(i + 1);

        // Métricas compiladas
        int promedioPuntos = miembrosDto.isEmpty() ? 0 :
                (int) miembrosDto.stream().mapToInt(MiembroGrupoResponse::getPuntosAcumulados).average().orElse(0);
        int totalSesiones = miembrosDto.stream().mapToInt(MiembroGrupoResponse::getTotalSesiones).sum();
        String masActivo = miembrosDto.isEmpty() ? "—" :
                miembrosDto.get(0).getNombre() + " " + miembrosDto.get(0).getApellido();

        // Comparativa por juego
        Map<String, List<Double>> progresosPorJuego = new HashMap<>();
        for (Estudiante e : miembros) {
            List<ProgresoJuego> progresos = progresoJuegoRepository.findByEstudianteId(e.getId());
            for (ProgresoJuego p : progresos) {
                String nombre = p.getJuego().getNombre();
                double pct = p.getJuego().getMaxNiveles() > 0
                        ? (p.getNivelActual() * 100.0 / p.getJuego().getMaxNiveles()) : 0;
                progresosPorJuego.computeIfAbsent(nombre, k -> new ArrayList<>()).add(pct);
            }
        }

        List<ComparativaJuegoResponse> comparativa = progresosPorJuego.entrySet().stream()
                .map(e -> ComparativaJuegoResponse.builder()
                        .juegoNombre(e.getKey())
                        .promedioProgreso(e.getValue().stream().mapToDouble(d -> d).average().orElse(0))
                        .totalCompletados((int) e.getValue().stream().filter(d -> d >= 100).count())
                        .build())
                .sorted(Comparator.comparingDouble(ComparativaJuegoResponse::getPromedioProgreso).reversed())
                .collect(Collectors.toList());

        String juegoMasDominado = comparativa.isEmpty() ? "—" : comparativa.get(0).getJuegoNombre();

        return GrupoResumenResponse.builder()
                .id(grupo.getId())
                .nombre(grupo.getNombre())
                .totalMiembros(miembros.size())
                .fechaCreacion(grupo.getFechaCreacion().toString())
                .miembros(miembrosDto)
                .promedioPuntos(promedioPuntos)
                .totalSesionesGrupo(totalSesiones)
                .alumnoMasActivo(masActivo)
                .juegoMasDominado(juegoMasDominado)
                .comparativaJuegos(comparativa)
                .build();
    }

    @Override
    public List<Map<String, Object>> getHistorialActividad(UUID grupoId) {
        GrupoEstudio grupo = grupoRepository.findById(grupoId)
                .orElseThrow(() -> new RuntimeException("Grupo no encontrado"));

        List<Map<String, Object>> historial = new ArrayList<>();

        for (Estudiante e : grupo.getEstudiantes()) {
            List<SesionJuego> sesiones = sesionJuegoRepository.findByEstudianteId(e.getId());
            for (SesionJuego s : sesiones) {
                Map<String, Object> entry = new java.util.LinkedHashMap<>();
                entry.put("estudianteNombre", e.getUsuario().getNombres() + " " + e.getUsuario().getApellidos());
                entry.put("juegoNombre", s.getProgreso().getJuego().getNombre());
                entry.put("fecha", s.getFechaSesion().toLocalDate().toString());
                entry.put("puntosObtenidos", s.getPuntosObtenidos());
                entry.put("completado", s.getCompletado());
                historial.add(entry);
            }
        }

        historial.sort(Comparator.comparing(m -> m.get("fecha").toString()));
        Collections.reverse(historial);
        return historial;
    }
}