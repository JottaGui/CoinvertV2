package projeto_integrado.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import projeto_integrado.dto.AuthenticationDTO;
import projeto_integrado.dto.LoginResponseDTO;
import projeto_integrado.dto.RegistreDTO;
import projeto_integrado.Infra.EmailService;
import projeto_integrado.Entidades.User;
import projeto_integrado.Infra.TokenService;
import projeto_integrado.Repositories.RepositorioUser;

	@Controller
	public class Authenticationcontroller {

	//	@GetMapping
	//	public String mostrarlogin() {
		//		return "Login";
			//}

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


		@PostMapping("/login")
		public String login(@ModelAttribute @Valid AuthenticationDTO data,
							HttpServletResponse response) {
			try {
				var auth = authenticationManager.authenticate(
						new UsernamePasswordAuthenticationToken(data.email(), data.senha())
				);
				var token = tokenService.generateToken((User) auth.getPrincipal());
				Cookie cookie = new Cookie("AUTH_TOKEN", token);
				cookie.setHttpOnly(true);
				cookie.setPath("/");
				cookie.setMaxAge(2 * 60 * 60);
				response.addCookie(cookie);
				return "redirect:/logado";
			} catch (Exception e) {
				return "email ou senha invalidos";
			}
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
