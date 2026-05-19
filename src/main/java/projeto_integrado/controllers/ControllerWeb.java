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
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.OpcambioRepo;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.RegistreDTO;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class ControllerWeb {

    @Autowired
    private RepositorioUser  userRepository;

    @Autowired
    private final OpcambioRepo opcambioRepo;

    @Autowired
    private PasswordEncoder passwordEncoder;

    RedirectAttributes redirectAttributes;

    public ControllerWeb(OpcambioRepo opcambioRepo) {
        this.opcambioRepo = opcambioRepo;
    }

    @GetMapping("/")
    public String home() {
        return "redirect:/Coinvert";
    }

    @GetMapping("/recuperar-senha")
    public String mostrarFormularioRecuperarSenha() {
        return "recuperar-senha";
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

    @GetMapping("/perfil/deletar")
    String deletuser(@AuthenticationPrincipal User principal,  Model model, RedirectAttributes redirectAttributes){
        User usuario = userRepository.findByEmail(principal.getEmail());

       try {
           userRepository.delete(usuario);
           return "redirect:/cadastro";
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("erro_delete", "Sua conta possui transaçoes e por isso nao " +
                   "pode ser deletada imediatamente. Ela será anlisada e deletada em ate 24hrs. Obrigado!");
        }
        return "redirect:/dash";
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

        if (moeda == null || moeda.isBlank()) {
            moeda = "USD";
        }

        moeda = moeda.trim().toUpperCase();

        BigDecimal total = opcambioRepo.somarMoedaPorUsuario(moeda, usuario.getId());

        List<OpCambio> operacoes = opcambioRepo.buscarOperacoes( usuario.getId());

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("moedaSelecionada", moeda);
        model.addAttribute("totalMoeda", total);
        model.addAttribute("operacoes", operacoes);

        return "dashboard";
    }


    @PostMapping("/cadastro")
    public String register(@ModelAttribute @Valid RegistreDTO data,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        String cpfformatado = data.cpf().replaceAll("\\D","");
        if (userRepository.findByCpf(cpfformatado) != null) {
            redirectAttributes.addFlashAttribute("erro", "CPF já cadastrado!");
            return "redirect:/cadastro";
        }

        if (userRepository.findByEmail(data.email()) != null) {
            redirectAttributes.addFlashAttribute("erro", "E-mail já cadastrado!");
            return "redirect:/cadastro";
        }


       try {
        String encryptedPassword = passwordEncoder.encode(data.senha());
        User newUser = new User(data.email(), encryptedPassword, data.nome(), cpfformatado);
           userRepository.save(newUser);
           redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso!");
           return "redirect:/login";
       } catch (Exception e) {
           redirectAttributes.addFlashAttribute("erro", "Erro ao realizar cadastro. Tente novamente.");
           return "redirect:/cadastro";
       }
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
