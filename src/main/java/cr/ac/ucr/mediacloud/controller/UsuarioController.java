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

import cr.ac.ucr.mediacloud.model.Rol;
import cr.ac.ucr.mediacloud.model.Usuario;
import cr.ac.ucr.mediacloud.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;

/**
 * Controller for user management.
 */
@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder passwordEncoder;

    /**
     * Creates the user controller.
     */
    public UsuarioController(UsuarioRepository usuarios,
                             BCryptPasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Shows the user list.
     */
    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuarios", usuarios.listar());
        return "usuarios/lista";
    }

    /**
     * Shows the form to create a user.
     */
    @GetMapping("/nuevo")
    public String nuevo(Model model, HttpSession session) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Usuario usuario = new Usuario();
        usuario.setActivo(true);
        usuario.setRol(Rol.USUARIO);

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("modo", "crear");

        return "usuarios/form";
    }

    /**
     * Saves a new user.
     */
    @PostMapping("/guardar")
    public String guardar(@ModelAttribute("usuario") Usuario usuario,
                          @RequestParam String claveTexto,
                          HttpSession session,
                          Model model) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()
                || claveTexto == null || claveTexto.isBlank()) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("modo", "crear");
            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "usuarios/form";
        }

        if (usuarios.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("modo", "crear");
            model.addAttribute("error", "Ya existe un usuario con ese correo.");
            return "usuarios/form";
        }

        usuario.setClave(passwordEncoder.encode(claveTexto));
        usuario.setActivo(true);

        if (usuario.getRol() == null) {
            usuario.setRol(Rol.USUARIO);
        }

        usuarios.guardar(usuario);

        return "redirect:/usuarios";
    }

    /**
     * Shows the form to edit a user.
     */
    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Integer id,
                         Model model,
                         HttpSession session) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Usuario usuario = usuarios.buscar(id).orElseThrow();

        model.addAttribute("usuario", usuario);
        model.addAttribute("roles", Rol.values());
        model.addAttribute("modo", "editar");

        return "usuarios/form";
    }

    /**
     * Updates an existing user.
     */
    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute("usuario") Usuario usuario,
                             @RequestParam(required = false) String claveTexto,
                             HttpSession session,
                             Model model) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        usuario.setId(id);

        if (usuario.getNombre() == null || usuario.getNombre().isBlank()
                || usuario.getCorreo() == null || usuario.getCorreo().isBlank()) {

            model.addAttribute("usuario", usuario);
            model.addAttribute("roles", Rol.values());
            model.addAttribute("modo", "editar");
            model.addAttribute("error", "Nombre y correo son obligatorios.");
            return "usuarios/form";
        }

        Usuario actual = usuarios.buscar(id).orElseThrow();

        if (usuarios.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            Usuario existente = usuarios.buscarPorCorreo(usuario.getCorreo()).get();

            if (!existente.getId().equals(id)) {
                model.addAttribute("usuario", usuario);
                model.addAttribute("roles", Rol.values());
                model.addAttribute("modo", "editar");
                model.addAttribute("error", "Ya existe otro usuario con ese correo.");
                return "usuarios/form";
            }
        }

        if (usuario.getRol() == null) {
            usuario.setRol(actual.getRol());
        }

        if (claveTexto != null && !claveTexto.isBlank()) {
            usuario.setClave(passwordEncoder.encode(claveTexto));
            usuarios.actualizarConClave(usuario);
        } else {
            usuarios.actualizar(usuario);
        }

        return "redirect:/usuarios";
    }

    /**
     * Deactivates a user.
     */
    @PostMapping("/desactivar/{id}")
    public String desactivar(@PathVariable Integer id,
                             HttpSession session) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        /*
         * Prevents the administrator from deactivating themselves.
         */
        if (usuarioSesion.getId() != null && usuarioSesion.getId().equals(id)) {
            return "redirect:/usuarios";
        }

        Usuario usuarioADesactivar = usuarios.buscar(id).orElseThrow();

        /*
         * Prevents administrator users from being deactivated.
         */
        if (usuarioADesactivar.getRol() != null
                && usuarioADesactivar.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/usuarios";
        }

        usuarios.eliminar(id);

        return "redirect:/usuarios";
    }

    /**
     * Deletes a user.
     */
    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        Usuario usuarioSesion = (Usuario) session.getAttribute("usuario");

        if (usuarioSesion == null) {
            return "redirect:/login";
        }

        /*
         * Protects this route if a button uses /eliminar.
         */
        if (usuarioSesion.getId() != null && usuarioSesion.getId().equals(id)) {
            return "redirect:/usuarios";
        }

        Usuario usuarioAEliminar = usuarios.buscar(id).orElseThrow();

        if (usuarioAEliminar.getRol() != null
                && usuarioAEliminar.getRol() == Rol.ADMINISTRADOR) {
            return "redirect:/usuarios";
        }

        usuarios.eliminar(id);

        return "redirect:/usuarios";
    }

    /**
     * Checks if the session user is an active administrator.
     */
    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null
                && usuario.getRol() != null
                && usuario.getRol() == Rol.ADMINISTRADOR
                && usuario.isActivo();
    }
}