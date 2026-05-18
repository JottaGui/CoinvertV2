package projeto_integrado.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Repositories.OpcambioRepo;
import projeto_integrado.enums.Estatus;

import java.util.List;
import java.util.Map;

@Service
public class Verificadordepagamento {


    private final OpcambioRepo repository;

    @Value("${api.v1.mercadopago-access-token}")
    private String accessToken;

    private final RestTemplate restTemplate = new RestTemplate();

    public Verificadordepagamento(OpcambioRepo repository) {
        this.repository = repository;
    }

    @Scheduled(fixedRate = 10000)
    public void verificarpendentes(){

        List<Long> idsPendentes = repository.buscarIdsPendentes(Estatus.PENDENTE);

        for (Long id : idsPendentes) {
            try {
                verificarPagamento(id);
            } catch (Exception e) {
                System.out.println("Erro ao verificar pagamento id " + id);
            }
        }


    }


    private void verificarPagamento(Long id) {
        String url = "https://api.mercadopago.com/v1/payments/search?external_reference=" + id;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<Map> response = restTemplate.exchange(
                url,
                HttpMethod.GET,
                entity,
                Map.class
        );

        List<Map<String, Object>> results =
                (List<Map<String, Object>>) response.getBody().get("results");

        if (results == null || results.isEmpty()) {
            System.out.println("Nenhum pagamento encontrado para ID: " + id);
            return;
        }

        String status = results.get(0).get("status").toString();

        if ("approved".equals(status)) {
            atualizarStatus(id);
            System.out.println("Pagamento aprovado para ID: " + id);
        } else {
            System.out.println("Pagamento encontrado, mas status = " + status + " para ID: " + id);
        }
    }

    private void atualizarStatus(Long id) {
        OpCambio oper = repository.findById(id).orElseThrow();
        oper.setEstatus(Estatus.APROVADA);
        repository.save(oper);
    }
}
