package med.voll.api.infra.security;

// Importaciones de Jakarta Servlet para el manejo de filtros HTTP
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

// Importaciones de Spring Framework
import med.voll.api.domain.usuario.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

// Importación para manejo de excepciones de entrada/salida
import java.io.IOException;

/**
 * @Component: Anotación que marca esta clase como un componente de Spring.
 * Spring la detectará automáticamente durante el escaneo de componentes
 * y la registrará como un bean en el contenedor de IoC (Inversión de Control).
 * Esto permite que Spring la inyecte donde sea necesario.
 */
@Component

/**
 * SecurityFilter: Filtro de seguridad personalizado para interceptar peticiones HTTP
 * y validar tokens JWT antes de que lleguen a los controladores.
 *
 * Extiende OncePerRequestFilter, una clase abstracta de Spring que garantiza que
 * este filtro se ejecute EXACTAMENTE UNA VEZ por cada petición HTTP,
 * evitando ejecuciones duplicadas en caso de forwards o includes internos del servidor.
 */
public class SecurityFilter extends OncePerRequestFilter {

    @Autowired
    private UsuarioRepository repository;

    @Autowired
    private TokenService tokenService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // Llama al metodo recuperarToken() para extraer el token JWT del header "Authorization"
        // La palabra clave 'var' permite que Java infiera automáticamente el tipo (String en este caso)
        var tokenJWT = recuperarToken(request);
        if(tokenJWT != null) {
//        obtenermos el subject
            var subject = tokenService.getSubject(tokenJWT);
            var usuario = repository.findByLogin(subject);
            var authentication = new UsernamePasswordAuthenticationToken(usuario, null, usuario.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

        }


        // IMPORTANTE: Continúa con la cadena de filtros
        // Esto pasa la petición al siguiente filtro o al controlador si es el último filtro
        // Si no se llama a este metodo, la petición se quedará bloqueada aquí y nunca llegará al endpoint
        filterChain.doFilter(request, response);
    }

    // Metodo privado auxiliar que extrae el token JWT del header "Authorization" de la petición HTTP.
    private String recuperarToken(HttpServletRequest request) {

        // Obtiene el valor del header "Authorization" de la petición HTTP
        // request.getHeader() retorna null si el header no existe
        var authorizationHeader = request.getHeader("Authorization");

        // Validación: Si el header Authorization no existe (es null), lanza una excepción
        // ⚠️ PROBLEMA: Esto bloqueará TODAS las peticiones que no tengan token,
        // incluyendo endpoints públicos como /login que no deberían requerir autenticación
        // SOLUCIÓN RECOMENDADA: Cambiar a "return null;" para permitir peticiones sin token
        if(authorizationHeader != null){
            return authorizationHeader.replace("Bearer ","");
        }
        return null;
    }
}