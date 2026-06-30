package cr.ac.ucr.mediacloud.config;

import cr.ac.ucr.mediacloud.model.Usuario;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Interceptor for authentication and admin access.
 */
@Component
public class AuthInterceptor implements HandlerInterceptor {

    /**
     * Checks each request before it reaches the controller.
     */
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String uri = request.getRequestURI();

        /**
         * Allows public routes.
         */
        if (esRutaPublica(uri)) {
            return true;
        }

        HttpSession session = request.getSession(false);

        /**
         * Redirects to login if there is no session.
         */
        if (session == null || session.getAttribute("usuario") == null) {
            response.sendRedirect("/login");
            return false;
        }

        Usuario usuario = (Usuario) session.getAttribute("usuario");

        /**
         * Blocks admin routes for non-admin users.
         */
        if (esRutaAdmin(uri) && !esAdministrador(usuario)) {
            response.sendRedirect("/dashboard");
            return false;
        }

        return true;
    }

    /**
     * Checks if the route is public.
     */
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

    /**
     * Checks if the route is only for admins.
     */
    private boolean esRutaAdmin(String uri) {
        return uri.startsWith("/usuarios");
    }

    /**
     * Checks if the user is an administrator.
     */
    private boolean esAdministrador(Usuario usuario) {
        return usuario != null
                && usuario.getRol() != null
                && "ADMINISTRADOR".equals(usuario.getRol().name());
    }
}