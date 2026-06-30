package cr.ac.ucr.mediacloud.controller;

import cr.ac.ucr.mediacloud.model.Categoria;
import cr.ac.ucr.mediacloud.repository.CategoriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository repo;

    public CategoriaController(CategoriaRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public String listar(Model m) {
        m.addAttribute("categorias", repo.listar());
        return "categorias/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria) {
        if (categoria.getId() == null)
            repo.guardar(categoria);
        else
            repo.actualizar(categoria);

        return "redirect:/categorias";
    }

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model m) {
        m.addAttribute("categoria", repo.buscar(id).orElseThrow());
        return "categorias/form";
    }

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        repo.eliminar(id);
        return "redirect:/categorias";
    }
}