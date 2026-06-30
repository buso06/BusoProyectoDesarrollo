package cr.ac.ucr.mediacloud.controller;

import cr.ac.ucr.mediacloud.model.Categoria;
import cr.ac.ucr.mediacloud.repository.CategoriaRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for category management.
 */
@Controller
@RequestMapping("/categorias")
public class CategoriaController {

    private final CategoriaRepository categorias;

    /**
     * Creates the category controller.
     */
    public CategoriaController(CategoriaRepository categorias) {
        this.categorias = categorias;
    }

    /**
     * Shows the category list.
     */
    @GetMapping
    public String listar(Model model) {
        model.addAttribute("categorias", categorias.listar());
        return "categorias/lista";
    }

    /**
     * Shows the form to create a category.
     */
    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("categoria", new Categoria());
        return "categorias/form";
    }

    /**
     * Saves a new category.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Categoria categoria, Model model) {

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "categorias/form";
        }

        categorias.guardar(categoria);
        return "redirect:/categorias";
    }

    /**
     * Shows the form to edit a category.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model model) {
        Categoria categoria = categorias.buscar(id).orElseThrow();

        model.addAttribute("categoria", categoria);
        return "categorias/form";
    }

    /**
     * Updates an existing category.
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Categoria categoria,
                             Model model) {

        if (categoria.getNombre() == null || categoria.getNombre().isBlank()) {
            categoria.setId(id);
            model.addAttribute("categoria", categoria);
            model.addAttribute("error", "El nombre de la categoría es obligatorio.");
            return "categorias/form";
        }

        categoria.setId(id);
        categorias.actualizar(categoria);

        return "redirect:/categorias";
    }

    /**
     * Deletes a category.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        categorias.eliminar(id);
        return "redirect:/categorias";
    }
}