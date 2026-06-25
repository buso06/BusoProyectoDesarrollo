package cr.ac.ucr.mediacloud.repository;

import cr.ac.ucr.mediacloud.model.Rol;
import cr.ac.ucr.mediacloud.model.Usuario;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepository {

    private final JdbcTemplate jdbc;

    public UsuarioRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Usuario> mapper = (rs, n) -> {
        Usuario u = new Usuario();

        u.setId(rs.getInt("id"));
        u.setNombre(rs.getString("nombre"));
        u.setCorreo(rs.getString("correo"));
        u.setClave(rs.getString("clave"));
        u.setRol(Rol.valueOf(rs.getString("rol")));
        u.setActivo(rs.getBoolean("activo"));

        Timestamp t = rs.getTimestamp("fecha_creacion");
        if (t != null) {
            u.setFechaCreacion(t.toLocalDateTime());
        }

        return u;
    };

    public List<Usuario> listar() {
        return jdbc.query(
                "SELECT * FROM usuarios ORDER BY id DESC",
                mapper
        );
    }

    public Optional<Usuario> buscar(Integer id) {
        return jdbc.query(
                "SELECT * FROM usuarios WHERE id = ?",
                mapper,
                id
        ).stream().findFirst();
    }

    public Optional<Usuario> buscarPorCorreo(String correo) {
        return jdbc.query(
                "SELECT * FROM usuarios WHERE correo = ?",
                mapper,
                correo
        ).stream().findFirst();
    }

    public void guardar(Usuario u) {
        jdbc.update(
                "INSERT INTO usuarios(nombre, correo, clave, rol, activo) VALUES (?, ?, ?, ?, ?)",
                u.getNombre(),
                u.getCorreo(),
                u.getClave(),
                u.getRol().name(),
                u.isActivo()
        );
    }

    public void actualizar(Usuario u) {
        jdbc.update(
                "UPDATE usuarios SET nombre = ?, correo = ?, rol = ?, activo = ? WHERE id = ?",
                u.getNombre(),
                u.getCorreo(),
                u.getRol().name(),
                u.isActivo(),
                u.getId()
        );
    }

    public void actualizarConClave(Usuario u) {
        jdbc.update(
                "UPDATE usuarios SET nombre = ?, correo = ?, clave = ?, rol = ?, activo = ? WHERE id = ?",
                u.getNombre(),
                u.getCorreo(),
                u.getClave(),
                u.getRol().name(),
                u.isActivo(),
                u.getId()
        );
    }

    public void actualizarClave(Integer id, String claveEncriptada) {
        jdbc.update(
                "UPDATE usuarios SET clave = ? WHERE id = ?",
                claveEncriptada,
                id
        );
    }

    public void eliminar(Integer id) {
        jdbc.update(
                "UPDATE usuarios SET activo = 0 WHERE id = ?",
                id
        );
    }

    public int total() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM usuarios WHERE activo = 1",
                Integer.class
        );
    }
}