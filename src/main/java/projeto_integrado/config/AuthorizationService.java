package projeto_integrado.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;

@Service
public class AuthorizationService implements UserDetailsService {


    @Autowired
    RepositorioUser repositorioUser;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User usuario = repositorioUser.findByEmail(email);

        if (usuario == null) {
            throw new UsernameNotFoundException("Usuário não encontrado");
        }

        return usuario; // User deve implementar UserDetails
    }
}
