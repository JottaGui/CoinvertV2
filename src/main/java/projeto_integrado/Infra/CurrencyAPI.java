package projeto_integrado.Infra;

import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.time.DateTimeException;
import java.time.LocalDate;

import org.json.JSONObject;
import projeto_integrado.Erros.Erros;

@Service
public class CurrencyAPI {


    String origem;
    String destino;
    Integer ano;
    Integer mes;
    Integer dia;

    public String obterCotacao(String origem, String destino) {
        try {
        	 destino = destino.toUpperCase();
             origem = origem.toUpperCase();
             
            String url = "https://economia.awesomeapi.com.br/json/last/" + origem + "-" + destino;
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            JSONObject pegarJson = new JSONObject(response.body());
            return pegarJson.getJSONObject(origem + destino).getString("bid");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public String valoremadata(String origem, String destino, Integer ano, Integer mes, Integer dia) {
        try {
            LocalDate data;
            try {
                data = LocalDate.of(ano, mes, dia);
            } catch (DateTimeException e) {
                System.out.println("Data inválida: " + ano + "-" + mes + "-" + dia);
                return null;
            }

            LocalDate hoje = LocalDate.now();
            if (data.isAfter(hoje)) {
                System.out.println("Data futura não permitida: " + data);
                return null;
            }

            destino = destino.toUpperCase();
            origem = origem.toUpperCase();

            String dataStr = data.toString();
            String url = "https://api.frankfurter.app/" + dataStr + "?from=" + origem + "&to=" + destino;

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .build();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("User-Agent", "Java 11 HttpClient")
                    .GET()
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                System.out.println("Cotação não disponível para a data: " + dataStr + " (HTTP " + response.statusCode() + ")");
                return null;
            }

            JSONObject json = new JSONObject(response.body());
            double valor = json.getJSONObject("rates").getDouble(destino);
            double valorFormatado = Math.round(valor * 100) / 100.0;

            return String.valueOf(valorFormatado);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
