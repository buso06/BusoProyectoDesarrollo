package cr.ac.ucr.mediacloud.controller;

import cr.ac.ucr.mediacloud.repository.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for the dashboard page.
 */
@Controller
public class DashboardController {

    private final UsuarioRepository u;
    private final AlbumRepository a;
    private final ArchivoRepository ar;
    private final CategoriaRepository c;

    /**
     * Creates the dashboard controller.
     */
    public DashboardController(UsuarioRepository u,
                               AlbumRepository a,
                               ArchivoRepository ar,
                               CategoriaRepository c) {
        this.u = u;
        this.a = a;
        this.ar = ar;
        this.c = c;
    }

    /**
     * Shows the dashboard with system totals.
     */
    @GetMapping("/dashboard")
    public String dash(Model m) {
        m.addAttribute("totalUsuarios", u.total());
        m.addAttribute("totalAlbumes", a.total());
        m.addAttribute("totalArchivos", ar.total());
        m.addAttribute("totalCategorias", c.total());

        return "dashboard/index";
    }
}