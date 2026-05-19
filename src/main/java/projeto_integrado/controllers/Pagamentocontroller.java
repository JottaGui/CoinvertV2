package projeto_integrado.controllers;


import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import projeto_integrado.Entidades.User;
import projeto_integrado.dto.OpCambioDTO;
import projeto_integrado.service.Opcambioservice;

@Controller
@RequestMapping("/cambio")
public class PagamentoController {

    private final Opcambioservice service;
    public PagamentoController(Opcambioservice service) {
        this.service = service;
    }

    @PostMapping("/comprar")
    public String comprar(@ModelAttribute OpCambioDTO dto,
                                        Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
                return "/";
            }
            String linkPagamento = service.criarPagamento(dto, user);
            return "redirect:" + linkPagamento;
        } catch (Exception e) {
            return  "/";
        }
    }

    @GetMapping("/pagamento/sucesso")
    public String pagamentoSucesso(RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute("sucesso", "Pagamento realizado com sucesso!");

        return "redirect:/dashboard";
    }
}
