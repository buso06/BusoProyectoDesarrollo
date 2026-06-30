package cr.ac.ucr.mediacloud.repository;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.mediacloud.model.ArchivoMultimedia;
import cr.ac.ucr.mediacloud.model.EstadoArchivo;

@Repository
public class ArchivoRepository {

    private final JdbcTemplate jdbc;

    public ArchivoRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<ArchivoMultimedia> mapper = (rs, n) -> {
        ArchivoMultimedia a = new ArchivoMultimedia();

        a.setId(rs.getInt("id"));
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setAlbumId((Integer) rs.getObject("album_id"));
        a.setCategoriaId((Integer) rs.getObject("categoria_id"));
        a.setNombreOriginal(rs.getString("nombre_original"));
        a.setNombreGuardado(rs.getString("nombre_guardado"));
        a.setTipoMime(rs.getString("tipo_mime"));
        a.setTamanioBytes(rs.getLong("tamanio_bytes"));
        a.setRutaRelativa(rs.getString("ruta_relativa"));
        a.setEstado(EstadoArchivo.valueOf(rs.getString("estado")));

        Timestamp fechaCarga = rs.getTimestamp("fecha_carga");
        if (fechaCarga != null) {
            a.setFechaCarga(fechaCarga.toLocalDateTime());
        }

        Timestamp fechaEliminacion = rs.getTimestamp("fecha_eliminacion");
        if (fechaEliminacion != null) {
            a.setFechaEliminacion(fechaEliminacion.toLocalDateTime());
        }

        return a;
    };

    public List<ArchivoMultimedia> listar() {
        return jdbc.query(
                "SELECT * FROM archivos_multimedia WHERE estado <> 'ELIMINADO' ORDER BY id DESC",
                mapper
        );
    }

    public List<ArchivoMultimedia> filtrar(String estado, Integer categoriaId) {
        String sql = "SELECT * FROM archivos_multimedia WHERE 1=1";
        List<Object> args = new ArrayList<>();

        if (estado != null && !estado.isBlank()) {
            sql += " AND estado = ?";
            args.add(estado);
        }

        if (categoriaId != null) {
            sql += " AND categoria_id = ?";
            args.add(categoriaId);
        }

        sql += " ORDER BY id DESC";

        return jdbc.query(sql, mapper, args.toArray());
    }

    public List<ArchivoMultimedia> listarPorAlbum(Integer albumId) {
        return jdbc.query(
                "SELECT * FROM archivos_multimedia " +
                "WHERE album_id = ? AND estado <> 'ELIMINADO' " +
                "ORDER BY id DESC",
                mapper,
                albumId
        );
    }

    public Optional<ArchivoMultimedia> buscar(Integer id) {
        return jdbc.query(
                "SELECT * FROM archivos_multimedia WHERE id = ?",
                mapper,
                id
        ).stream().findFirst();
    }

    public void guardar(ArchivoMultimedia a) {
        jdbc.update(
                "INSERT INTO archivos_multimedia " +
                "(usuario_id, album_id, categoria_id, nombre_original, nombre_guardado, tipo_mime, tamanio_bytes, ruta_relativa, estado) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)",
                a.getUsuarioId(),
                a.getAlbumId(),
                a.getCategoriaId(),
                a.getNombreOriginal(),
                a.getNombreGuardado(),
                a.getTipoMime(),
                a.getTamanioBytes(),
                a.getRutaRelativa(),
                a.getEstado().name()
        );
    }

    public void actualizarDatos(Integer id, Integer albumId, Integer categoriaId, String estado) {
        jdbc.update(
                "UPDATE archivos_multimedia SET album_id = ?, categoria_id = ?, estado = ? WHERE id = ?",
                albumId,
                categoriaId,
                estado,
                id
        );
    }

    public void papelera(Integer id) {
        jdbc.update(
                "UPDATE archivos_multimedia SET estado = 'EN_PAPELERA', fecha_eliminacion = NOW() WHERE id = ?",
                id
        );
    }

    public void eliminar(Integer id) {
        jdbc.update(
                "DELETE FROM archivos_multimedia WHERE id = ?",
                id
        );
    }

    public int total() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM archivos_multimedia WHERE estado = 'ACTIVO'",
                Integer.class
        );
    }

    public List<Map<String, Object>> reporteCategoria() {
        return jdbc.queryForList(
                "SELECT COALESCE(c.nombre, 'Sin categoria') categoria, COUNT(a.id) cantidad " +
                "FROM archivos_multimedia a " +
                "LEFT JOIN categorias c ON a.categoria_id = c.id " +
                "WHERE a.estado <> 'ELIMINADO' " +
                "GROUP BY c.nombre " +
                "ORDER BY cantidad DESC"
        );
    }
}