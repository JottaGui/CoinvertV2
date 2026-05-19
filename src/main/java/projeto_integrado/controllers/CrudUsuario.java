package projeto_integrado.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.Entidades.User;
import projeto_integrado.dto.RegistreDTO;

import java.util.Objects;


@Controller
@RequestMapping("/Perfil")
public class CrudUsuario {

    private final RepositorioUser repositorioUser;

    public CrudUsuario(RepositorioUser repositorioUser) {
        this.repositorioUser = repositorioUser;
    }
    @Autowired
    private PasswordEncoder passwordEncoder;

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
                               RedirectAttributes redirectAttributes,
                               @RequestParam(required = false) String senha,
                               @AuthenticationPrincipal User principal, RegistreDTO data) {
        if (principal == null) {
            return "redirect:/";
        }

        User usuario = repositorioUser.findByEmail(principal.getEmail());

        usuario.setNome(nome);
        usuario.setEmail(email);

        long iduser = principal.getId();

        if (senha != null && !senha.isEmpty()) {

            String encodedsenha = passwordEncoder.encode(data.senha());
            usuario.setSenha(encodedsenha);
        }
        if (repositorioUser.existsByEmailAndIdNot(email, iduser)){
            redirectAttributes.addFlashAttribute("erro", "Este e-mail já está sendo usado.");
            return "redirect:/Perfil";
        }
try {
    repositorioUser.save(usuario);
    redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
    return "redirect:/Perfil";
} catch (Exception e) {
    redirectAttributes.addFlashAttribute("erro", "Este e-mail já está sendo usado.");
    return "redirect:/Perfil";
}
    }

}
