package projeto_integrado.controllers;


import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.RegistreDTO;

@Controller
public class ControllerWeb {

    @Autowired
    private RepositorioUser  userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String home() {
        return "redirect:/Coinvert";
    }

    @GetMapping("/login")
    public String mostrarlogin() {
        return "Login";
    }

    @GetMapping("/cadastro")
    public String mostrarcadastro() {
        return "Cadastro";
    }

    @GetMapping("/perfil")
    public String perfilpagina() {
        return "perfil";
    }
    @PostMapping("/cadastro")
    public String register(@ModelAttribute @Valid RegistreDTO data) {
        if (userRepository.findByEmail(data.email()) != null) {
            return "email ja cadastrado";
        }
        String encryptedPassword = passwordEncoder.encode(data.senha());
        User newUser = new User(data.email(), encryptedPassword, data.nome());
        userRepository.save(newUser);

        return "redirect:/login";
    }

    @GetMapping("/logado")
    public String showLoggedPage(@AuthenticationPrincipal User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }
        model.addAttribute("usuario", principal);
        return "logado";
    }

    @GetMapping("/logout2")
    public String logout(HttpServletResponse response) {
        // JWT é stateless: "logout" = apagar token no cliente (cookie)
        Cookie cookie = new Cookie("AUTH_TOKEN", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        response.addCookie(cookie);

        return "redirect:/Coinvert";
    }
}
