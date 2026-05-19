package projeto_integrado.controllers;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Entidades.User;
import projeto_integrado.Infra.EmailService;
import projeto_integrado.Infra.TokenService;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.AuthenticationDTO;

@Controller
public class AuthController {

    @Autowired
    private RepositorioUser userRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private TokenService tokenService;

    @GetMapping("/login")
    public String mostrarLogin() {
        return "Login";
    }

    @PostMapping({"/login"})
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

            return "redirect:/logado";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "E-mail ou senha inválidos.");
            return "redirect:/login";
        }
    }

    @GetMapping("/recuperar-senha")
    public String mostrarFormularioRecuperarSenha() {
        return "recuperar-senha";
    }

    @PostMapping({"/recuperar-senha", "/recuperar-senha-method"})
    public String recuperarSenha(@RequestParam String email,
                                 RedirectAttributes redirectAttributes) {
        String emailNormalizado = email == null ? "" : email.trim().toLowerCase();
        User usuario = userRepository.findByEmail(emailNormalizado);

        if (usuario != null) {
            String assunto = "Recuperação de Senha - CoinVert";
            String corpo = "Olá, " + usuario.getNome() + "\n\n" +
                    "Recebemos uma solicitação de recuperação de senha para sua conta CoinVert.\n" +
                    "Por segurança, sua senha atual não é enviada por e-mail.\n\n" +
                    "Caso você tenha solicitado essa recuperação, siga o fluxo de redefinição configurado na aplicação.";

            emailService.enviarEmail(emailNormalizado, assunto, corpo);
        }

        redirectAttributes.addFlashAttribute(
                "mensagem",
                "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação."
        );
        return "redirect:/recuperar-senha";
    }

    @GetMapping({"/logout"})
    public String logout(HttpServletResponse response) {
        Cookie cookie = new Cookie("AUTH_TOKEN", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/coinvert";
    }
}
