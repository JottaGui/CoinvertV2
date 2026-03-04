package projeto_integrado.controllers;

import com.mercadopago.MercadoPagoConfig;
import com.mercadopago.client.preference.PreferenceClient;
import com.mercadopago.client.preference.PreferenceItemRequest;
import com.mercadopago.client.preference.PreferenceRequest;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.preference.Preference;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import projeto_integrado.Infra.CurrencyAPI;
import projeto_integrado.Entidades.OpCambio;
import projeto_integrado.Repositories.OpcambioRepo;
import projeto_integrado.Repositories.RepositorioUser;
import projeto_integrado.dto.OpCambioDTO;
import projeto_integrado.enums.Estatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class Opcambioservice {

    private static final Logger log = LoggerFactory.getLogger(Opcambioservice.class);

    @Value("${api.v1.mercadopago-access-token}")
    private String accessToken;

    private final OpcambioRepo repository;
    private final CurrencyAPI api;
    private final RepositorioUser repositorioUser;

    public Opcambioservice(OpcambioRepo repository, CurrencyAPI api, RepositorioUser repositorioUser) {
        this.repository = repository;
        this.api = api;
        this.repositorioUser = repositorioUser;
    }



    @PostConstruct
    public void init(){
    MercadoPagoConfig.setAccessToken(accessToken);
    log.info("iniciando mercado pado");}

    public String criarPagamento(OpCambioDTO dto) throws MPException, MPApiException {

        OpCambio oper = new OpCambio();

        oper.setMoeda(dto.getMoeda());
        oper.setQuantidademoeda(dto.getQuantidadeMoeda());

        String cotacaoatual = api.obterCotacao(oper.getMoeda(), "BRL");

        BigDecimal cotacao = new BigDecimal(cotacaoatual);
        BigDecimal valor = cotacao
                .multiply(oper.getQuantidademoeda())
                .setScale(2, RoundingMode.HALF_UP);

        oper.setCotacao(cotacao);
        oper.setValor(valor);
        oper.setEstatus(Estatus.PENDENTE);

        oper = repository.save(oper);

        PreferenceItemRequest itemRequest =
                PreferenceItemRequest.builder()
                        .id(String.valueOf(oper.getId()))
                        .title("Compra de " + oper.getMoeda())
                        .quantity(1)
                        .unitPrice(oper.getValor())
                        .currencyId("BRL")
                        .build();

        List<PreferenceItemRequest> items = new ArrayList<>();
        items.add(itemRequest);

        PreferenceRequest preferenceRequest =
                PreferenceRequest.builder()
                        .items(items)
                        .externalReference(String.valueOf(oper.getId()))
                        .build();

        PreferenceClient client = new PreferenceClient();

        Preference preference = client.create(preferenceRequest);

        oper.setMpPreferenceId(preference.getId());
        repository.save(oper);

        return preference.getInitPoint();
    }
}



