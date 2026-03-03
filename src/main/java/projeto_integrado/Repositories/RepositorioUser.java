package projeto_integrado.Repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import projeto_integrado.Entidades.User;

public interface RepositorioUser extends JpaRepository<User, Integer> {

	 User findByEmail(String email);
}
