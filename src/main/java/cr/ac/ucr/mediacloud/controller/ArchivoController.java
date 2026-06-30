package cr.ac.ucr.mediacloud.controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import cr.ac.ucr.mediacloud.model.ArchivoMultimedia;
import cr.ac.ucr.mediacloud.model.EstadoArchivo;
import cr.ac.ucr.mediacloud.model.Usuario;
import cr.ac.ucr.mediacloud.repository.AlbumRepository;
import cr.ac.ucr.mediacloud.repository.ArchivoRepository;
import cr.ac.ucr.mediacloud.repository.CategoriaRepository;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for multimedia file management.
 */
@Controller
@RequestMapping("/archivos")
public class ArchivoController {

    private final ArchivoRepository archivos;
    private final AlbumRepository albumes;
    private final CategoriaRepository categorias;

    @Value("${mediacloud.upload-dir}")
    private String uploadDir;

    /**
     * Creates the file controller.
     */
    public ArchivoController(ArchivoRepository archivos,
                             AlbumRepository albumes,
                             CategoriaRepository categorias) {
        this.archivos = archivos;
        this.albumes = albumes;
        this.categorias = categorias;
    }

    /**
     * Shows the file list with filters.
     */
    @GetMapping
    public String listar(@RequestParam(required = false) String estado,
                         @RequestParam(required = false) Integer categoriaId,
                         Model m) {

        m.addAttribute("archivos", archivos.filtrar(estado, categoriaId));
        m.addAttribute("categorias", categorias.listar());
        m.addAttribute("estado", estado);
        m.addAttribute("categoriaId", categoriaId);
        m.addAttribute("estados", EstadoArchivo.values());

        return "archivos/lista";
    }

    /**
     * Shows the form to upload a file.
     */
    @GetMapping("/nuevo")
    public String nuevo(Model m) {
        m.addAttribute("albumes", albumes.listar());
        m.addAttribute("categorias", categorias.listar());
        return "archivos/form";
    }

    /**
     * Saves an uploaded file.
     */
    @PostMapping("/guardar")
    public String guardar(@RequestParam MultipartFile archivo,
                          @RequestParam(required = false) Integer albumId,
                          @RequestParam(required = false) Integer categoriaId,
                          HttpSession s) throws Exception {

        Usuario u = (Usuario) s.getAttribute("usuario");

        if (u == null) {
            return "redirect:/login";
        }

        if (archivo.isEmpty()) {
            return "redirect:/archivos/nuevo?error=vacio";
        }

        Path carpeta = Paths.get(uploadDir, String.valueOf(u.getId()));
        Files.createDirectories(carpeta);

        String nombreOriginal = archivo.getOriginalFilename();

        if (nombreOriginal == null || nombreOriginal.isBlank()) {
            nombreOriginal = "archivo";
        }

        String nombreGuardado = UUID.randomUUID() + "-" + nombreOriginal;
        Path destino = carpeta.resolve(nombreGuardado);

        Files.copy(archivo.getInputStream(), destino);

        ArchivoMultimedia a = new ArchivoMultimedia();
        a.setUsuarioId(u.getId());
        a.setAlbumId(albumId);
        a.setCategoriaId(categoriaId);
        a.setNombreOriginal(nombreOriginal);
        a.setNombreGuardado(nombreGuardado);
        a.setTipoMime(archivo.getContentType() == null ? "application/octet-stream" : archivo.getContentType());
        a.setTamanioBytes(archivo.getSize());
        a.setRutaRelativa(destino.toString());
        a.setEstado(EstadoArchivo.ACTIVO);

        archivos.guardar(a);

        return "redirect:/archivos";
    }

    /**
     * Shows the form to edit a file.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id, Model m) {
        m.addAttribute("archivo", archivos.buscar(id).orElseThrow());
        m.addAttribute("albumes", albumes.listar());
        m.addAttribute("categorias", categorias.listar());
        m.addAttribute("estados", EstadoArchivo.values());
        return "archivos/editar";
    }

    /**
     * Updates file data.
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @RequestParam(required = false) Integer albumId,
                             @RequestParam(required = false) Integer categoriaId,
                             @RequestParam String estado) {

        archivos.actualizarDatos(id, albumId, categoriaId, estado);
        return "redirect:/archivos";
    }

    /**
     * Moves a file to trash.
     */
    @PostMapping("/papelera/{id}")
    public String papelera(@PathVariable Integer id) {
        archivos.papelera(id);
        return "redirect:/archivos";
    }

    /**
     * Deletes a file permanently.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id) {
        ArchivoMultimedia archivo = archivos.buscar(id).orElseThrow();

        try {
            if (archivo.getRutaRelativa() != null && !archivo.getRutaRelativa().isBlank()) {
                Path ruta = Paths.get(archivo.getRutaRelativa());

                if (Files.exists(ruta)) {
                    Files.delete(ruta);
                }
            }

            archivos.eliminar(id);

        } catch (Exception e) {
            throw new RuntimeException("No se pudo eliminar el archivo definitivamente", e);
        }

        return "redirect:/archivos";
    }

    /**
     * Opens a file in the browser.
     */
    @GetMapping("/ver/{id}")
    public ResponseEntity<InputStreamResource> ver(@PathVariable Integer id) throws Exception {
        ArchivoMultimedia archivo = archivos.buscar(id).orElseThrow();

        Path ruta = Paths.get(archivo.getRutaRelativa());

        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }

        String nombreLimpio = archivo.getNombreOriginal().replace("\"", "");

        InputStreamResource recurso = new InputStreamResource(Files.newInputStream(ruta));

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(archivo.getTipoMime()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + nombreLimpio + "\"")
                .body(recurso);
    }

    /**
     * Downloads a file.
     */
    @GetMapping("/descargar/{id}")
    public ResponseEntity<InputStreamResource> descargar(@PathVariable Integer id) throws Exception {
        ArchivoMultimedia archivo = archivos.buscar(id).orElseThrow();

        Path ruta = Paths.get(archivo.getRutaRelativa());

        if (!Files.exists(ruta)) {
            return ResponseEntity.notFound().build();
        }

        String nombreLimpio = archivo.getNombreOriginal().replace("\"", "");

        InputStreamResource recurso = new InputStreamResource(Files.newInputStream(ruta));

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + nombreLimpio + "\"")
                .body(recurso);
    }
}