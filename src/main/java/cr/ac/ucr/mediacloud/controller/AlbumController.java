package cr.ac.ucr.mediacloud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

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

    public AlbumController(AlbumRepository albumes, ArchivoRepository archivos) {
        this.albumes = albumes;
        this.archivos = archivos;
    }

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("albumes", albumes.listar());
        return "albumes/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("album", new Album());
        return "albumes/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Album album, HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        if (usuario == null) {
            return "redirect:/login";
        }

        album.setUsuarioId(usuario.getId());
        albumes.guardar(album);

        return "redirect:/albumes";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        model.addAttribute("album", albumes.buscar(id).orElseThrow());
        return "albumes/form";
    }

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Album album) {
        album.setId(id);
        albumes.actualizar(album);

        return "redirect:/albumes";
    }

    @GetMapping("/ver/{id}")
    public String ver(@PathVariable Integer id, Model model) {
        Album album = albumes.buscar(id).orElseThrow();

        model.addAttribute("album", album);
        model.addAttribute("archivos", archivos.listarPorAlbum(id));

        return "albumes/detalle";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        albumes.eliminar(id);
        return "redirect:/albumes";
    }
}