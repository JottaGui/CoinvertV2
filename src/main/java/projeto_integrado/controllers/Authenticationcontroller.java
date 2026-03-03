package projeto_integrado.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpSession;
import projeto_integrado.Entidades.AuthenticationDTO;
import projeto_integrado.Entidades.LoginResponseDTO;
import projeto_integrado.Entidades.RegistreDTO;
import projeto_integrado.Infra.EmailService;
import projeto_integrado.Entidades.User;
import projeto_integrado.Infra.TokenService;
import projeto_integrado.Repositories.RepositorioUser;

	@Controller
	@RequestMapping("/login")
	public class Authenticationcontroller {

		@GetMapping
		public String mostrarlogin() {
				return "Login";
			}

		@Autowired
		private RepositorioUser  userRepository;
		@Autowired
		private EmailService emailService;
		@Autowired
		private AuthenticationManager authenticationManager;
		@Autowired
		private PasswordEncoder passwordEncoder;
		@Autowired
		TokenService tokenService;


		/*@PostMapping("/login")
		public String verificaLogin(@RequestParam String email, @RequestParam String senha, HttpSession session, Model model) {
			User usuario = userRepository.findByEmail(email);

			if (usuario != null && usuario.getSenha().equals(senha)) {
				session.setAttribute("usuariologado", usuario);
				session.setMaxInactiveInterval(900);
				return "redirect:/logado";
			} else {
				return "redirect:/login";
			}
		}
*/

	@PostMapping("/login2")
		public ResponseEntity login(@RequestBody @Valid AuthenticationDTO data){
		var usernamePassword = new UsernamePasswordAuthenticationToken(data.email(), data.senha());
		var auth = this.authenticationManager.authenticate(usernamePassword);



		var token = tokenService.generateToken((User) auth.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
	}

		@PostMapping("/cadastro")
		public ResponseEntity register (@RequestBody @Valid RegistreDTO data){
			if (this.userRepository.findByEmail(data.email()) != null ) return ResponseEntity.badRequest().build();

			String encryptedPassword = passwordEncoder.encode(data.senha());
			User newUser = new User (data.email(), encryptedPassword, data.nome());
			this.userRepository.save(newUser);
			return ResponseEntity.ok().build();
		}

		@GetMapping("/recuperar-senha")
		public String mostrarFormularioRecuperarSenha() {
		    return "recuperar-senha"; 
		}

		@PostMapping("/recuperar-senha")
		public String recuperarSenha(@RequestParam String email, Model model) {
			User usuario = userRepository.findByEmail(email);

			if (usuario != null) {
				String assunto = "Recuperação de Senha";
				String corpo = "Olá, " + usuario.getNome() + "\n\nSua senha é: " + usuario.getSenha();
				emailService.enviarEmail(email, assunto, corpo);
				model.addAttribute("mensagem", "Senha enviada para o e-mail.");
			} else {
				model.addAttribute("mensagem", "E-mail não encontrado.");
			}

			return "recuperar-Senha";
		}



	}
