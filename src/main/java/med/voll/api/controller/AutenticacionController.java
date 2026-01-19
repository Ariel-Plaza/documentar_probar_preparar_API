package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.usuario.DatosAutenticacion;
import med.voll.api.domain.usuario.Usuario;
import med.voll.api.infra.security.DatosTokenJWT;
import med.voll.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// @RestController: Indica que esta clase es un controlador REST que maneja peticiones HTTP
// y automáticamente serializa las respuestas a JSON
@RestController
// @RequestMapping: Define la ruta base para todos los endpoints de este controlador
// Todas las peticiones a "/login" serán manejadas aquí
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired
    private TokenService tokenService;

    // @Autowired: Inyección de dependencias de Spring
    // Spring Boot configurará automáticamente esta instancia
    @Autowired
    private AuthenticationManager manager;

    // @PostMapping: Indica que este metodo maneja peticiones HTTP POST a "/login"
    @PostMapping
    public ResponseEntity iniciarSesion(
            // @RequestBody: Convierte el JSON recibido en un objeto DatosAutenticacion
            // @Valid: Activa la validación de los datos según las anotaciones en DatosAutenticacion
            @RequestBody @Valid DatosAutenticacion datos){

        // Crea un token de autenticación con las credenciales recibidas
        // UsernamePasswordAuthenticationToken es la estructura que Spring Security
        // usa para representar credenciales de usuario/contraseña
        var authenticationToken = new UsernamePasswordAuthenticationToken(
                datos.login(),      // Usuario
                datos.contrasena()  // Contraseña
        );

        // manager.authenticate() intenta autenticar al usuario
        // Si las credenciales son correctas, devuelve un objeto Authentication
        // Si son incorrectas, lanza una excepción (AuthenticationException)
        var autenticacion = manager.authenticate(authenticationToken);

        var tokenJWT = tokenService.generarToken((Usuario) autenticacion.getPrincipal());

        // Devuelve una respuesta HTTP 200 (OK) sin contenido en el body
        // retornamos un objeto DTO en base a DatosTokenJWT
        return ResponseEntity.ok(new DatosTokenJWT(tokenJWT));
    }
}
