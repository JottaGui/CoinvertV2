package projeto_integrado.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired; 
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import jakarta.validation.Valid;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;

@Controller
@RequestMapping("/cadastro")
public class Cadastrojpa {

	@GetMapping
	public String mostrarcadastro() {
		return "Cadastro";
	}
		@Autowired
		private RepositorioUser repositorioUser;
		
		@PostMapping
		public String usuario(@Valid User user) {
			repositorioUser.save(user);
			return "login";
		}
		
		@GetMapping(path="/{id}")
		public Optional<User> retornausuarios(@PathVariable int id) {
			return repositorioUser.findById(id);
		}
		
		@PutMapping
		public User alteraruser(@Valid User user) {
			return repositorioUser.save(user);
			
		}
		
		@DeleteMapping(path="/{id}")
		public void deletaruser(@PathVariable int id) {
		    repositorioUser.deleteById(id);
		}
		
}
