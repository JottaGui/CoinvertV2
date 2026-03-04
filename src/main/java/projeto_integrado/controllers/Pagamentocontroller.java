package projeto_integrado.controllers;

import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import projeto_integrado.dto.OpCambioDTO;
import projeto_integrado.service.Opcambioservice;

@RestController
@RequestMapping("/cambio")
public class Pagamentocontroller {

    private final Opcambioservice service;

    public Pagamentocontroller(Opcambioservice service) {
        this.service = service;
    }

        @PostMapping("/comprar")
        public ResponseEntity<String> comprar(@RequestBody OpCambioDTO dto, HttpSession session) throws MPException, MPApiException {


            //long userid = (long) session.getAttribute(session.getId());
            String linkPagamento = service.criarPagamento(dto);

            return ResponseEntity.ok(linkPagamento);
        }

}
