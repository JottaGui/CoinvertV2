package projeto_integrado.controllers;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.RegistreDTO;

@Controller
public class CadastroController {

    @Autowired
    private RepositorioUser userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @GetMapping("/cadastro")
    public String mostrarCadastro() {
        return "Cadastro";
    }

    @PostMapping("/cadastro")
    public String register(@ModelAttribute @Valid RegistreDTO data,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("erro", "Preencha todos os campos corretamente.");
            return "redirect:/cadastro";
        }

        String nome = data.nome() == null ? "" : data.nome().trim();
        String email = data.email() == null ? "" : data.email().trim().toLowerCase();
        String cpfFormatado = data.cpf() == null ? "" : data.cpf().replaceAll("\\D", "");

        if (!cpfValido(cpfFormatado)) {
            redirectAttributes.addFlashAttribute("erro", "CPF inválido.");
            return "redirect:/cadastro";
        }

        if (userRepository.findByCpf(cpfFormatado) != null) {
            redirectAttributes.addFlashAttribute("erro", "CPF já cadastrado!");
            return "redirect:/cadastro";
        }

        if (userRepository.findByEmail(email) != null) {
            redirectAttributes.addFlashAttribute("erro", "E-mail já cadastrado!");
            return "redirect:/cadastro";
        }

        try {
            String encryptedPassword = passwordEncoder.encode(data.senha());
            User newUser = new User(email, encryptedPassword, nome, cpfFormatado);
            userRepository.save(newUser);

            redirectAttributes.addFlashAttribute("sucesso", "Cadastro realizado com sucesso. Faça login para continuar.");
            return "redirect:/login";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao realizar cadastro. Tente novamente.");
            return "redirect:/cadastro";
        }
    }

    private boolean cpfValido(String cpf) {
        if (cpf == null || cpf.length() != 11) {
            return false;
        }

        if (cpf.matches("(\\d)\\1{10}")) {
            return false;
        }

        int soma = 0;
        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (10 - i);
        }

        int primeiroDigito = (soma * 10) % 11;
        if (primeiroDigito == 10) {
            primeiroDigito = 0;
        }

        if (primeiroDigito != Character.getNumericValue(cpf.charAt(9))) {
            return false;
        }

        soma = 0;
        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpf.charAt(i)) * (11 - i);
        }

        int segundoDigito = (soma * 10) % 11;
        if (segundoDigito == 10) {
            segundoDigito = 0;
        }

        return segundoDigito == Character.getNumericValue(cpf.charAt(10));
    }
}
