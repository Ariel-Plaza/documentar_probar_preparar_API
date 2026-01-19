package med.voll.api.controller;

import jakarta.validation.Valid;
import med.voll.api.domain.usuarios.DatosAutenticacionUsuario;
import med.voll.api.domain.usuarios.Usuario;
import med.voll.api.domain.usuarios.UsuarioRepository;
import med.voll.api.infra.security.DatosJWTToken;
import med.voll.api.infra.security.TokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class AutenticacionController {

    @Autowired // <--- AGREGA ESTO
    private UsuarioRepository repository;

    @Autowired
    private PasswordEncoder passwordEncoder; // Importa org.springframework.security.crypto.password.PasswordEncoder

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @PostMapping
    public ResponseEntity autenticarUsuario(@RequestBody @Valid DatosAutenticacionUsuario datosAutenticacionUsuario) {
//------- VERIFICACION error 403 ------------
        // ESTO NOS DIRÁ QUÉ ESTÁ PASANDO REALMENTE
        System.out.println(">>> Intentando login con: " + datosAutenticacionUsuario.login());
        System.out.println(">>> Clave enviada (raw): " + datosAutenticacionUsuario.clave());

        // Verifiquemos si el usuario existe y qué clave tiene en la DB según Hibernate
        var usuarioEnDB = repository.findByLogin(datosAutenticacionUsuario.login());
        if (usuarioEnDB != null) {
            System.out.println(">>> Usuario encontrado en DB");
            System.out.println(">>> Hash en DB: " + usuarioEnDB.getPassword());
            System.out.println("NUEVO HASH GENERADO: " + passwordEncoder.encode("123456"));
            // Comparación manual rápida para probar el bean
            boolean coinciden = passwordEncoder.matches(datosAutenticacionUsuario.clave(), usuarioEnDB.getPassword());
            System.out.println(">>> ¿Coinciden según BCrypt?: " + coinciden);
        } else {
            System.out.println(">>> USUARIO NO ENCONTRADO EN DB");
        }
//---------------FIN error 403--------------------
        Authentication authToken = new UsernamePasswordAuthenticationToken(datosAutenticacionUsuario.login(),
                datosAutenticacionUsuario.clave());
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }

}
