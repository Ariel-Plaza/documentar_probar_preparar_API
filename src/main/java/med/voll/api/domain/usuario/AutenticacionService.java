package med.voll.api.domain.usuario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

//indicamos a spring que es un servicio
@Service
public class AutenticacionService implements UserDetailsService {
    @Autowired
    private UsuarioRepository repository;
    @Override
    //busca en la base de datos el usuario
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return repository.findByLogin(username);
    }
}
