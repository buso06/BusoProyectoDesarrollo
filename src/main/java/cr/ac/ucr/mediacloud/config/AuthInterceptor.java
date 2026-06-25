package cr.ac.ucr.mediacloud.config;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

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

        // Rutas públicas
        if (uri.equals("/")
                || uri.equals("/login")
                || uri.equals("/registro")
                || uri.equals("/error")
                || uri.equals("/favicon.ico")
                || uri.startsWith("/css/")
                || uri.startsWith("/js/")
                || uri.startsWith("/images/")
                || uri.startsWith("/img/")
                || uri.startsWith("/webjars/")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session != null && session.getAttribute("usuario") != null) {
            return true;
        }

        response.sendRedirect("/login");
        return false;
    }
}