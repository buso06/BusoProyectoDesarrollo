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

@Controller
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder passwordEncoder;

    public UsuarioController(UsuarioRepository usuarios,
                             BCryptPasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping
    public String listar(Model model, HttpSession session) {
        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        model.addAttribute("usuarios", usuarios.listar());
        return "usuarios/lista";
    }

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

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Usuario usuario,
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

    @PostMapping("/actualizar/{id}")
    public String actualizar(@PathVariable Integer id,
                             @ModelAttribute Usuario usuario,
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

        usuarios.buscarPorCorreo(usuario.getCorreo()).ifPresent(existente -> {
            if (!existente.getId().equals(id)) {
                throw new RuntimeException("Correo repetido");
            }
        });

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

    @PostMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Integer id,
                           HttpSession session) {

        if (!esAdmin(session)) {
            return "redirect:/dashboard";
        }

        usuarios.eliminar(id);

        return "redirect:/usuarios";
    }

    private boolean esAdmin(HttpSession session) {
        Usuario usuario = (Usuario) session.getAttribute("usuario");

        return usuario != null
                && usuario.getRol() != null
                && "ADMINISTRADOR".equals(usuario.getRol().name());
    }
}