package projeto_integrado.controllers;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projeto_integrado.Entidades.User;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "redirect:/coinvert";
    }

    @GetMapping("/logado")
    public String showLoggedPage(@AuthenticationPrincipal User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", principal);
        return "logado";
    }
}
