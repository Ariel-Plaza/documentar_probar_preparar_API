package med.voll.api.infra.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import med.voll.api.domain.usuario.Usuario;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Date;

//Servicio para trabajar con token
@Service

public class TokenService {
//    secret desde aplication.properties
@Value("${api.security.token.secret}")
    private String secret;
//    Encargado de generar tokens
    public String generarToken(Usuario usuario){
        try {
            //Algoritmo basado en creacion de token, HMAC256 es el algoritmo recibe un parametro String es la clave
            var algoritmo = Algorithm.HMAC256(secret);
//            comienza la creacionJWT
            return JWT.create()
                    //Cual es el servidor que firma
                    .withIssuer("API Voll.med")
                    //que usuario se logeo
                    .withSubject(usuario.getLogin())
//                    tiempo de uso del token
                    .withExpiresAt(fechaExpiracion())
                    //firma token
                    .sign(algoritmo);
        } catch (JWTCreationException exception){
            // Invalid Signing configuration / Couldn't convert Claims.
            throw new RuntimeException("Error al generar el token JWT", exception);
        }
    }

    private Instant fechaExpiracion() {
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    // Método que valida y decodifica un token JWT, extrayendo el "subject" (generalmente el username o email del usuario)
    public String getSubject(String tokenKWT){
        try {
            // Crea el algoritmo HMAC256 usando la clave secreta para verificar la firma del token
            var algoritmo = Algorithm.HMAC256(secret);
            // Construye un verificador JWT, valida que el issuer sea correcto, verifica la firma del token y extrae el subject
            return JWT.require(algoritmo)
                    // Valida que el token haya sido creado por nuestro servidor (issuer debe ser "API Voll.med")
                    .withIssuer("API Voll.med")
                    // Construye el verificador con las configuraciones especificadas
                    .build()
                    // Verifica la firma, la expiración y la validez del token
                    .verify(tokenKWT)
                    // Extrae y retorna el "subject" del token (el identificador del usuario, como email o username)
                    .getSubject();
        } catch (JWTVerificationException exception){
            // Si el token es inválido, está expirado o la firma no coincide, lanza una excepción con mensaje descriptivo
            throw new RuntimeException("Token JWT invalido o expirado!");
        }
    }
}
