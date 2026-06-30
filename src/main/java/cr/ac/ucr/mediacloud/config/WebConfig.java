package cr.ac.ucr.mediacloud.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Configuration class used to register web-related settings.
 * It adds the authentication interceptor to the application.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    /**
     * Interceptor used to validate user authentication and route access.
     */
    private final AuthInterceptor authInterceptor;

    /**
     * Creates the web configuration with the authentication interceptor.
     *
     * @param authInterceptor the interceptor used to protect application routes
     */
    public WebConfig(AuthInterceptor authInterceptor) {
        this.authInterceptor = authInterceptor;
    }

    /**
     * Registers the authentication interceptor for all application routes.
     *
     * @param registry the registry used to add interceptors
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(authInterceptor)
                .addPathPatterns("/**");
    }
}