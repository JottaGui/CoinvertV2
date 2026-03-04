package projeto_integrado.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.Entidades.User;



@Controller
@RequestMapping("/Perfil")
public class CrudUsuario {

    private final RepositorioUser repositorioUser;

    public CrudUsuario(RepositorioUser repositorioUser) {
        this.repositorioUser = repositorioUser;
    }

    @GetMapping
    public String Perfil(@AuthenticationPrincipal User principal, Model model) {
        if (principal == null) {
            return "/login";
        }
        model.addAttribute("usuario", principal);
        return "Perfil";
    }

    @PostMapping("/editar-dados")
    public String salvarEdicao(@RequestParam String nome,
                               @RequestParam String email,
                               @RequestParam(required = false) String senha,
                               @AuthenticationPrincipal User principal) {
        if (principal == null) {
            return "redirect:/";
        }

        User usuario = repositorioUser.findByEmail(principal.getEmail());
        if (usuario == null) {
            return "redirect:/";
        }
        usuario.setNome(nome);
        usuario.setEmail(email);

        if (senha != null && !senha.isEmpty()) {
            usuario.setSenha(senha);
        }

        repositorioUser.save(usuario);

        return "redirect:/Perfil";
    }

}
