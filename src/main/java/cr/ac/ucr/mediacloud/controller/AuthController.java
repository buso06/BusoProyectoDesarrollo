package cr.ac.ucr.mediacloud.controller;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import cr.ac.ucr.mediacloud.model.Rol;
import cr.ac.ucr.mediacloud.model.Usuario;
import cr.ac.ucr.mediacloud.repository.UsuarioRepository;
import jakarta.servlet.http.HttpSession;

@Controller
public class AuthController {

    private final UsuarioRepository usuarios;
    private final BCryptPasswordEncoder passwordEncoder;

    public AuthController(UsuarioRepository usuarios,
                          BCryptPasswordEncoder passwordEncoder) {
        this.usuarios = usuarios;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping({"/", "/login"})
    public String login() {
        return "auth/login";
    }

    @PostMapping("/login")
    public String iniciarSesion(@RequestParam String correo,
                                @RequestParam String clave,
                                HttpSession session) {

        Optional<Usuario> encontrado = usuarios.buscarPorCorreo(correo);

        if (encontrado.isEmpty()) {
            return "redirect:/login?error=true";
        }

        Usuario usuario = encontrado.get();

        if (!usuario.isActivo()) {
            return "redirect:/login?error=true";
        }

        String claveGuardada = usuario.getClave();
        boolean claveCorrecta;

        if (claveGuardada != null && claveGuardada.startsWith("$2")) {
            claveCorrecta = passwordEncoder.matches(clave, claveGuardada);
        } else {
            String sha256 = sha256(clave);
            claveCorrecta = sha256.equals(claveGuardada);

            if (claveCorrecta) {
                String nuevaClave = passwordEncoder.encode(clave);
                usuarios.actualizarClave(usuario.getId(), nuevaClave);
                usuario.setClave(nuevaClave);
            }
        }

        if (!claveCorrecta) {
            return "redirect:/login?error=true";
        }

        session.setAttribute("usuario", usuario);
        session.setAttribute("rol", usuario.getRol().name());

        return "redirect:/dashboard";
    }

    @GetMapping("/registro")
    public String registro(Model model) {
        model.addAttribute("usuario", new Usuario());
        return "auth/registro";
    }

    @PostMapping("/registro")
    public String registrarUsuario(@RequestParam String nombre,
                                   @RequestParam String correo,
                                   @RequestParam String clave,
                                   @RequestParam String confirmarClave,
                                   Model model) {

        if (nombre == null || nombre.isBlank()
                || correo == null || correo.isBlank()
                || clave == null || clave.isBlank()
                || confirmarClave == null || confirmarClave.isBlank()) {

            model.addAttribute("error", "Todos los campos son obligatorios.");
            return "auth/registro";
        }

        if (!clave.equals(confirmarClave)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            return "auth/registro";
        }

        if (usuarios.buscarPorCorreo(correo).isPresent()) {
            model.addAttribute("error", "Ya existe un usuario registrado con ese correo.");
            return "auth/registro";
        }

        Usuario nuevo = new Usuario();
        nuevo.setNombre(nombre);
        nuevo.setCorreo(correo);
        nuevo.setClave(passwordEncoder.encode(clave));
        nuevo.setRol(Rol.USUARIO);
        nuevo.setActivo(true);

        usuarios.guardar(nuevo);

        return "redirect:/login?registro=ok";
    }

    @PostMapping("/logout")
    public String cerrarSesion(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }

    private String sha256(String texto) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(texto.getBytes(StandardCharsets.UTF_8));

            StringBuilder hex = new StringBuilder();

            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) {
                    hex.append('0');
                }
                hex.append(h);
            }

            return hex.toString();

        } catch (Exception e) {
            throw new RuntimeException("Error al encriptar con SHA-256", e);
        }
    }
}