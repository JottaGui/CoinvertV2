package projeto_integrado.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Entidades.User;
import projeto_integrado.Repositories.OpcambioRepo;
import projeto_integrado.Repositories.RepositorioUser;

import java.math.BigDecimal;
import java.util.List;

@Controller
public class DashboardController {

    @Autowired
    private RepositorioUser userRepository;

    @Autowired
    private OpcambioRepo opcambioRepo;

    @GetMapping({"/dashboard"})
    public String mostrarDashboard(@AuthenticationPrincipal User principal,
                                   @RequestParam(required = false) String moeda,
                                   Model model) {
        if (principal == null) {
            return "redirect:/login";
        }

        User usuario = userRepository.findByEmail(principal.getEmail());

        if (usuario == null) {
            return "redirect:/login";
        }

        if (moeda == null || moeda.isBlank()) {
            moeda = "USD";
        }

        moeda = moeda.trim().toUpperCase();

        BigDecimal total = opcambioRepo.somarMoedaPorUsuario(moeda, usuario.getId());
        List<OpCambio> operacoes = opcambioRepo.buscarOperacoes(usuario.getId());

        if (total == null) {
            total = BigDecimal.ZERO;
        }

        model.addAttribute("usuario", usuario);
        model.addAttribute("moedaSelecionada", moeda);
        model.addAttribute("totalMoeda", total);
        model.addAttribute("operacoes", operacoes);

        return "dashboard";
    }
}
