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

import java.util.Date;
import java.util.List;
import java.util.Map;

import static org.springframework.data.jpa.domain.AbstractPersistable_.id;

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



    @Scheduled(fixedRate = 240000)
    public void apagarpendentes(){
        List<OpCambio> operacoesPendentes = repository.findByEstatus(Estatus.PENDENTE);
        for (OpCambio oper : operacoesPendentes) {
            try {
                long id = oper.getId();
                Date data = oper.getData_op();
                Date agora = new Date();
                long difereca =  agora.getTime() - data.getTime();

                if (difereca > 240000) {
                    System.out.println("preference de ID " + oper.getId() + " apagada");
                    repository.deleteById(oper.getId());
                } else {
                    System.out.println("Nenhuma preference apagada");
                }
                } catch (Exception e) {
                    System.out.println("Erro ao verificar pagamento id " + oper.getId());
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
