package projeto_integrado.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.RegistreDTO;

@Controller
@RequestMapping("/cad")
public class Cadastrojpa {


		@Autowired
		private RepositorioUser repositorioUser;
		@Autowired
		private PasswordEncoder passwordEncoder;


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
