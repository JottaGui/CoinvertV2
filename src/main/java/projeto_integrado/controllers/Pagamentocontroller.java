package projeto_integrado.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto_integrado.Entidades.User;
import projeto_integrado.dto.OpCambioDTO;
import projeto_integrado.service.Opcambioservice;

@RestController
@RequestMapping("/cambio")
public class PagamentoController {

    private final Opcambioservice service;

    public PagamentoController(Opcambioservice service) {
        this.service = service;
    }

    @PostMapping("/comprar")
    public ResponseEntity<Void> comprar(@ModelAttribute OpCambioDTO dto,
                                        Authentication authentication) {
        try {
            if (authentication == null || !(authentication.getPrincipal() instanceof User user)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            String linkPagamento = service.criarPagamento(dto, user);

            return ResponseEntity.ok()
                    .header("HX-Redirect", linkPagamento)
                    .build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
