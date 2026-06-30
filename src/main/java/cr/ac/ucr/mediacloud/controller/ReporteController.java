package cr.ac.ucr.mediacloud.controller;

import cr.ac.ucr.mediacloud.repository.ArchivoRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller for report pages.
 */
@Controller
public class ReporteController {

    private final ArchivoRepository archivos;

    /**
     * Creates the report controller.
     */
    public ReporteController(ArchivoRepository archivos) {
        this.archivos = archivos;
    }

    /**
     * Shows the category report.
     */
    @GetMapping("/reportes")
    public String reportes(Model m) {
        m.addAttribute("reporteCategorias", archivos.reporteCategoria());
        return "reportes/index";
    }
}