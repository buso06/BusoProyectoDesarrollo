package cr.ac.ucr.mediacloud.controller;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cr.ac.ucr.mediacloud.model.Album;
import cr.ac.ucr.mediacloud.model.Usuario;
import cr.ac.ucr.mediacloud.repository.AlbumRepository;
import cr.ac.ucr.mediacloud.repository.ArchivoRepository;
import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/albumes")
public class AlbumController {

    private final AlbumRepository albumes;
    private final ArchivoRepository archivos;
    private final BCryptPasswordEncoder passwordEncoder;

    public AlbumController(AlbumRepository albumes,
                           ArchivoRepository archivos,
                           BCryptPasswordEncoder passwordEncoder) {
        this.albumes = albumes;
        this.archivos = archivos;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("albumes", albumes.listar());
        return "albumes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        Album album = new Album();
        album.setPublico(true);
        album.setColor("#2563eb");

        model.addAttribute("album", album);
        model.addAttribute("modo", "crear");

        return "albumes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Album album,
                          @RequestParam(required = false) String claveTexto,
                          HttpSession session,
                          Model model) {

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        if (album.getNombre() == null || album.getNombre().isBlank()) {
            model.addAttribute("album", album);
            model.addAttribute("modo", "crear");
            model.addAttribute("error", "El nombre del álbum es obligatorio.");
            return "albumes/form";
        }

        album.setUsuarioId(usuario.getId());

        if (album.getColor() == null || album.getColor().isBlank()) {
            album.setColor("#2563eb");
        }

        if (!album.isPublico()) {
            if (claveTexto == null || claveTexto.isBlank()) {
                model.addAttribute("album", album);
                model.addAttribute("modo", "crear");
                model.addAttribute("error", "Los álbumes privados necesitan contraseña.");
                return "albumes/form";
            }

            album.setClave(passwordEncoder.encode(claveTexto));
        } else {
            album.setClave(null);
        }

        albumes.guardar(album);

        return "redirect:/albumes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Album album = albumes.buscar(id).orElseThrow();

        if (album.getColor() == null || album.getColor().isBlank()) {
            album.setColor("#2563eb");
        }

        model.addAttribute("album", album);
        model.addAttribute("modo", "editar");

        return "albumes/form";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Album album,
                             @RequestParam(required = false) String claveTexto,
                             Model model) {

        Album actual = albumes.buscar(id).orElseThrow();

        album.setId(id);
        album.setUsuarioId(actual.getUsuarioId());

        if (album.getNombre() == null || album.getNombre().isBlank()) {
            model.addAttribute("album", album);
            model.addAttribute("modo", "editar");
            model.addAttribute("error", "El nombre del álbum es obligatorio.");
            return "albumes/form";
        }

        if (album.getColor() == null || album.getColor().isBlank()) {
            album.setColor("#2563eb");
        }

        if (album.isPublico()) {
            album.setClave(null);
        } else {
            if (claveTexto != null && !claveTexto.isBlank()) {
                album.setClave(passwordEncoder.encode(claveTexto));
            } else {
                album.setClave(actual.getClave());
            }

            if (album.getClave() == null || album.getClave().isBlank()) {
                model.addAttribute("album", album);
                model.addAttribute("modo", "editar");
                model.addAttribute("error", "El álbum privado necesita una contraseña.");
                return "albumes/form";
            }
        }

        albumes.actualizar(album);

        return "redirect:/albumes";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id,
                      Model model,
                      HttpSession session) {

        Album album = albumes.buscar(id).orElseThrow();

        if (!album.isPublico()) {
            Boolean autorizado = (Boolean) session.getAttribute(sessionKey(id));

            if (autorizado == null || !autorizado) {
                model.addAttribute("album", album);
                return "albumes/clave";
            }
        }

        model.addAttribute("album", album);
        model.addAttribute("archivos", archivos.listarPorAlbum(id));

        return "albumes/detalle";
    }

    @PostMapping("/validar-clave/{id}")
    public String validarClave(@PathVariable Integer id,
                               @RequestParam String claveTexto,
                               Model model,
                               HttpSession session) {

        Album album = albumes.buscar(id).orElseThrow();

        if (album.isPublico()) {
            return "redirect:/albumes/ver/" + id;
        }

        if (album.getClave() == null || album.getClave().isBlank()) {
            model.addAttribute("album", album);
            model.addAttribute("error", "Este álbum privado no tiene contraseña configurada.");
            return "albumes/clave";
        }

        if (!passwordEncoder.matches(claveTexto, album.getClave())) {
            model.addAttribute("album", album);
            model.addAttribute("error", "Contraseña incorrecta.");
            return "albumes/clave";
        }

        session.setAttribute(sessionKey(id), true);

        return "redirect:/albumes/ver/" + id;
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        albumes.eliminar(id);
        return "redirect:/albumes";
    }

    private String sessionKey(Integer id) {
        return "album_autorizado_" + id;
    }
}