package cr.ac.ucr.mediacloud.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import cr.ac.ucr.mediacloud.model.Categoria;

@Repository
public class CategoriaRepository {

    private final JdbcTemplate jdbc;

    public CategoriaRepository(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    private final RowMapper<Categoria> mapper = (rs, n) -> {
        Categoria c = new Categoria();

        c.setId(rs.getInt("id"));
        c.setNombre(rs.getString("nombre"));
        c.setDescripcion(rs.getString("descripcion"));

        return c;
    };

    public List<Categoria> listar() {
        return jdbc.query(
                "SELECT * FROM categorias ORDER BY nombre",
                mapper
        );
    }

    public Optional<Categoria> buscar(Integer id) {
        return jdbc.query(
                "SELECT * FROM categorias WHERE id=?",
                mapper,
                id
        ).stream().findFirst();
    }

    public void guardar(Categoria c) {
        jdbc.update(
                "INSERT INTO categorias(nombre,descripcion) VALUES(?,?)",
                c.getNombre(),
                c.getDescripcion()
        );
    }

    public void actualizar(Categoria c) {
        jdbc.update(
                "UPDATE categorias SET nombre=?, descripcion=? WHERE id=?",
                c.getNombre(),
                c.getDescripcion(),
                c.getId()
        );
    }

    public void eliminar(Integer id) {
        jdbc.update(
                "DELETE FROM categorias WHERE id=?",
                id
        );
    }

    public int total() {
        return jdbc.queryForObject(
                "SELECT COUNT(*) FROM categorias",
                Integer.class
        );
    }
}