package cr.ac.ucr.mediacloud.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import cr.ac.ucr.mediacloud.model.Album;

@Repository
public class AlbumRepository {

    private final JdbcTemplate jdbc;

    public AlbumRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Album> mapper = (rs, n) -> {
        Album a = new Album();

        a.setId(rs.getInt("id"));
        a.setUsuarioId(rs.getInt("usuario_id"));
        a.setNombre(rs.getString("nombre"));
        a.setDescripcion(rs.getString("descripcion"));
        a.setPublico(rs.getBoolean("publico"));
        a.setClave(rs.getString("clave"));
        a.setColor(rs.getString("color"));

        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) {
            a.setFechaCreacion(t.toLocalDateTime());
        }

        return a;
    };

    public List<Album> listar() {
        return jdbc.query(
                "SELECT * FROM albumes ORDER BY id DESC",
                mapper
        );
    }

    public List<Album> listarPorUsuario(Integer usuarioId, boolean admin) {
        if (admin) {
            return listar();
        }

        return jdbc.query(
                "SELECT * FROM albumes WHERE usuario_id = ? ORDER BY id DESC",
                mapper,
                usuarioId
        );
    }

    public Optional<Album> buscar(Integer id) {
        return jdbc.query(
                "SELECT * FROM albumes WHERE id = ?",
                mapper,
                id
        ).stream().findFirst();
    }

    public void guardar(Album a) {
        jdbc.update(
                "INSERT INTO albumes(usuario_id, nombre, descripcion, publico, clave, color) " +
                        "VALUES (?, ?, ?, ?, ?, ?)",
                a.getUsuarioId(),
                a.getNombre(),
                a.getDescripcion(),
                a.isPublico(),
                a.getClave(),
                a.getColor()
        );
    }

    public void actualizar(Album a) {
        jdbc.update(
                "UPDATE albumes SET nombre = ?, descripcion = ?, publico = ?, clave = ?, color = ? WHERE id = ?",
                a.getNombre(),
                a.getDescripcion(),
                a.isPublico(),
                a.getClave(),
                a.getColor(),
                a.getId()
        );
    }

    @Transactional
    public void eliminar(Integer id) {
        jdbc.update(
                "DELETE FROM archivos_compartidos WHERE archivo_id IN " +
                        "(SELECT id FROM archivos_multimedia WHERE album_id = ?)",
                id
        );

        jdbc.update(
                "DELETE FROM archivos_multimedia WHERE album_id = ?",
                id
        );

        jdbc.update(
                "DELETE FROM albumes WHERE id = ?",
                id
        );
    }

    public int total() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM albumes",
                Integer.class
        );
    }
}