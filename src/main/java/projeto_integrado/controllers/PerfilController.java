package projeto_integrado.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;

@Controller
public class PerfilController {

    @Autowired
    private RepositorioUser repositorioUser;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping({"/perfil"})
    public String perfil(@AuthenticationPrincipal User principal, Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User usuario = repositorioUser.findByEmail(principal.getEmail());

        if (usuario == null) {
            return "redirect:/login";
        }

        model.addAttribute("usuario", usuario);
        return "perfil";
    }

    @PostMapping({"/perfil/editar",})
    public String salvarEdicao(@RequestParam String nome,
                               @RequestParam String email,
                               @RequestParam(required = false) String senha,
                               @RequestParam(required = false) String confirmarSenha,
                               @AuthenticationPrincipal User principal,
                               RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User usuario = repositorioUser.findByEmail(principal.getEmail());

        if (usuario == null) {
            return "redirect:/login";
        }

        String nomeNormalizado = nome == null ? "" : nome.trim();
        String emailNormalizado = email == null ? "" : email.trim().toLowerCase();
        String senhaNormalizada = senha == null ? "" : senha.trim();
        String confirmarSenhaNormalizada = confirmarSenha == null ? "" : confirmarSenha.trim();

        if (nomeNormalizado.length() < 3) {
            redirectAttributes.addFlashAttribute("erro", "Informe um nome com pelo menos 3 caracteres.");
            return "redirect:/perfil";
        }

        if (emailNormalizado.isBlank()) {
            redirectAttributes.addFlashAttribute("erro", "Informe um e-mail válido.");
            return "redirect:/perfil";
        }

        if (repositorioUser.existsByEmailAndIdNot(emailNormalizado, usuario.getId())) {
            redirectAttributes.addFlashAttribute("erro", "Este e-mail já está sendo usado por outro usuário.");
            return "redirect:/perfil";
        }

        if (!senhaNormalizada.isBlank() || !confirmarSenhaNormalizada.isBlank()) {
            if (senhaNormalizada.length() < 6) {
                redirectAttributes.addFlashAttribute("erro", "A nova senha deve ter pelo menos 6 caracteres.");
                return "redirect:/perfil";
            }

            if (!senhaNormalizada.equals(confirmarSenhaNormalizada)) {
                redirectAttributes.addFlashAttribute("erro", "As senhas não conferem.");
                return "redirect:/perfil";
            }

            usuario.setSenha(passwordEncoder.encode(senhaNormalizada));
        }

        try {
            usuario.setNome(nomeNormalizado);
            usuario.setEmail(emailNormalizado);
            repositorioUser.save(usuario);

            redirectAttributes.addFlashAttribute("sucesso", "Dados atualizados com sucesso!");
            return "redirect:/perfil";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Não foi possível atualizar seus dados. Tente novamente.");
            return "redirect:/perfil";
        }
    }

    @GetMapping({"/perfil/deletar"})
    public String deletarUsuario(@AuthenticationPrincipal User principal,
                                 RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        User usuario = repositorioUser.findByEmail(principal.getEmail());

        if (usuario == null) {
            return "redirect:/login";
        }

        try {
            repositorioUser.delete(usuario);
            redirectAttributes.addFlashAttribute("sucesso", "Conta removida com sucesso.");
            return "redirect:/cadastro";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute(
                    "erro_delete",
                    "Sua conta possui transações e por isso não pode ser deletada imediatamente. Ela será analisada e deletada em até 24 horas. Obrigado!"
            );
            return "redirect:/dashboard";
        }
    }
}
