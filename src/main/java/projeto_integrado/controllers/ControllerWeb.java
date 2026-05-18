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
import org.springframework.web.bind.annotation.*;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.OpcambioRepo;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.RegistreDTO;

import java.math.BigDecimal;

@Controller
public class ControllerWeb {

    @Autowired
    private RepositorioUser  userRepository;

    @Autowired
    private final OpcambioRepo opcambioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public ControllerWeb(OpcambioRepo opcambioRepo) {
        this.opcambioRepo = opcambioRepo;
    }

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

    @GetMapping("/dash")
    public String mostrardash(@AuthenticationPrincipal User principal,
                              @RequestParam(required = false) String moeda,
                              Model model) {

        if (principal == null) {
            return "redirect:/";
        }

        User usuario = userRepository.findByEmail(principal.getEmail());

        if (usuario == null) {
            return "redirect:/";
        }

        // Define moeda padrão caso venha null ou vazia
        if (moeda == null || moeda.isBlank()) {
            moeda = "USD";
        }

        moeda = moeda.trim().toUpperCase();

        BigDecimal total = opcambioRepo.somarMoedaPorUsuario(moeda, usuario.getId());

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("moedaSelecionada", moeda);
        model.addAttribute("totalMoeda", total);

        return "dashboard";
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

    @GetMapping("/recuperar-senha")
    public String mostrarFormularioRecuperarSenha() {
        return "recuperar-senha";
    }


}
