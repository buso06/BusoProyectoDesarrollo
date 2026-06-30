package cr.ac.ucr.mediacloud.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import cr.ac.ucr.mediacloud.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        if (esRutaPublica(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("/login");
            return false;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        // Rutas solo para administrador
        if (esRutaAdmin(uri) && !esAdministrador(usuario)) {
            response.sendRedirect("/dashboard");
            return false;
        }

        return true;
    }

    private boolean esRutaPublica(String uri) {
        return uri.equals("/")
                || uri.equals("/login")
                || uri.equals("/registro")
                || uri.equals("/error")
                || uri.equals("/favicon.ico")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/img/")
                || uri.startsWith("/webjars/");
    }

    private boolean esRutaAdmin(String uri) {
        return uri.startsWith("/usuarios")
                || uri.startsWith("/categorias");
    }

    private boolean esAdministrador(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "ADMINISTRADOR".equals(usuario.getRol().name());
    }
}