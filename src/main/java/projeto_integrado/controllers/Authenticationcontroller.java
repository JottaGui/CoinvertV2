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

import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.dto.AuthenticationDTO;
import projeto_integrado.dto.LoginResponseDTO;
import projeto_integrado.dto.RegistreDTO;
import projeto_integrado.Infra.EmailService;
import projeto_integrado.Entidades.User;
import projeto_integrado.Infra.TokenService;
import projeto_integrado.Repositories.RepositorioUser;

	@Controller
	public class Authenticationcontroller {



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


		@PostMapping("/login2")
		public String login(@ModelAttribute @Valid AuthenticationDTO data,
							HttpServletResponse response,
							RedirectAttributes redirectAttributes) {
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
				redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso. Faça login para continuar.");
				return "redirect:/logado";
			} catch (Exception e) {
				redirectAttributes.addFlashAttribute("erro", "E-mail ou senha inválidos.");
				return "redirect:/login";
			}
		}


		@PostMapping("/recuperar-senha-method")
		public String recuperarSenha(@RequestParam String email,
									 RedirectAttributes redirectAttributes,
									 Model model) {
			User usuario = userRepository.findByEmail(email);


			if (usuario != null) {
				String assunto = "Recuperação de Senha";
				String corpo = "Olá, " + usuario.getNome() + "\n\nSua senha é: " + usuario.getSenha();
				emailService.enviarEmail(email, assunto, corpo);
				redirectAttributes.addFlashAttribute("mensagem", "Senha enviada para o e-mail.");
			} else {
				redirectAttributes.addFlashAttribute("mensagem", "E-mail não encontrado.");
			}
			return "recuperar-Senha";
		}



	}
