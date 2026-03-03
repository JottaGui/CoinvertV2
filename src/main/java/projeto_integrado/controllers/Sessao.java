package projeto_integrado.controllers;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import projeto_integrado.Entidades.User;

@Controller
public class Sessao {

    @GetMapping("/logado")
    public String paginaLogado(HttpSession session, Model model) {
        User usuario = (User) session.getAttribute("usuariologado");

        if (usuario == null) {
            return "redirect:/login"; // volta para a tela de login se não estiver logado
        }

        model.addAttribute("usuario", usuario);
        return "logado"; // a página de usuários logados
    }

}
