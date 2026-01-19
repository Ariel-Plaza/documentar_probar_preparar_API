package med.voll.api.domain.usuario;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.Collection;
import java.util.List;

// Indica que esta clase está mapeada a la tabla "usuarios" en la base de datos
@Table(name = "usuarios")
// Define el nombre de la entidad JPA como "Usuario"
@Entity(name = "Usuario")
// Lombok: Genera automáticamente los métodos getter para todos los campos
@Getter
// Lombok: Genera un constructor sin parámetros (requerido por JPA)
@NoArgsConstructor
// Lombok: Genera un constructor con todos los parámetros
@AllArgsConstructor
// Lombok: Genera equals() y hashCode() basados únicamente en el campo "id"
@EqualsAndHashCode(of = "id")
public class Usuario implements UserDetails {

    // Marca este campo como la clave primaria de la entidad
    @Id
    // Indica que el ID se generará automáticamente (auto-increment en la BD)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Campo que almacena el email/nombre de usuario
    private String login;

    // Campo que almacena la contraseña hasheada con BCrypt
    private String contrasena;

    /**
     * Retorna los roles/permisos del usuario
     * Spring Security usa este metodo para verificar qué puede hacer el usuario
     * @return Lista con el rol "ROLE_USER" asignado a todos los usuarios
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_USER"));
    }

    /**
     * Retorna la contraseña hasheada del usuario
     * Spring Security usa este metodo para comparar la contraseña ingresada
     * con el hash almacenado en la base de datos usando BCrypt
     * @return El hash de la contraseña (ej: $2a$10$xHj...)
     */
    @Override
    public String getPassword() {
        return contrasena;
    }

    /**
     * Retorna el nombre de usuario (login/email)
     * Spring Security usa este metodo para identificar al usuario
     * @return El email/login del usuario
     */
    @Override
    public String getUsername() {
        return login;
    }

    /**
     * Indica si la cuenta del usuario no ha expirado
     * @return true - la cuenta nunca expira
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario no está bloqueada
     * @return true - la cuenta nunca se bloquea
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * Indica si las credenciales (contraseña) del usuario no han expirado
     * @return true - las credenciales nunca expiran
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * Indica si la cuenta del usuario está habilitada/activa
     * @return true - la cuenta siempre está habilitada
     */
    @Override
    public boolean isEnabled() {
        return true;
    }
}