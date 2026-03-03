package projeto_integrado.controllers;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Autowired
    private RepositorioUser userRepository;

    @GetMapping
    public String Perfil (HttpSession session, Model model){
     User usuario = (User) session.getAttribute("usuariologado");
        if(usuario == null){
            return "/login";
        }

        model.addAttribute("usuario", usuario);
        return "Perfil";
    }


    @PostMapping("/editar-dados")
    public String salvarEdicao(@RequestParam String nome, @RequestParam String email, @RequestParam(required = false) String senha, HttpSession session) {
        User usuario = (User) session.getAttribute("usuariologado");

        if (usuario == null) {
            return "redirect:/";
        }

        usuario.setNome(nome);
        usuario.setEmail(email);
        if (senha != null && !senha.isEmpty()) {
            usuario.setSenha(senha);
        }

        userRepository.save(usuario); // atualiza no banco
        session.setAttribute("usuariologado", usuario); // atualiza sessão
       return "redirect:/Perfil";
    }




}
