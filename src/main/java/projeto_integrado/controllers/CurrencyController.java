package projeto_integrado.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDate;
import projeto_integrado.Infra.CurrencyAPI;

@Controller
@RequestMapping("/Coinvert")
public class CurrencyController {



    private CurrencyAPI api = new CurrencyAPI();

    @GetMapping
    public String paginapost() {
        return "Coinvert";
    }
    
    @PostMapping
    public String obterCotacao(@RequestParam String origem, @RequestParam String destino, @RequestParam double valor, Model model) {
        String valorco = this.api.obterCotacao(origem, destino);
        double valorcotacao = Double.parseDouble(valorco);

        Double valorconvertido = valor * valorcotacao;

        model.addAttribute("cotacao", valorcotacao);
        model.addAttribute("valorConvertido", valorconvertido);
        return "Coinvert :: resultado";
    }

    @PostMapping("/passado")
    public String valoremadata(@RequestParam String origem, @RequestParam String destino,@RequestParam Integer ano, @RequestParam Integer mes, @RequestParam Integer dia, @RequestParam double valor, Model model) {
        String Valoremdata = this.api.valoremadata(origem, destino, ano, mes, dia);
        double valorcotacaobefore = Double.parseDouble(Valoremdata);

        double Valoremdataconvertido = valor * valorcotacaobefore;

        model.addAttribute("valorcotacaobefore", valorcotacaobefore);
        model.addAttribute("Valoremdataconvertido", Valoremdataconvertido);
        return "";
    }

    @PostMapping("/simulacao")
    public String compararCotacao(
            @RequestParam String origem,
            @RequestParam String destino,
            @RequestParam double valor,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            Model model) {

        int ano = data.getYear();
        int mes = data.getMonthValue();
        int dia = data.getDayOfMonth();

        double cotacaoAtual = Double.parseDouble(api.obterCotacao(origem, destino));
        double cotacaoPassada = Double.parseDouble(api.valoremadata(origem, destino, ano, mes, dia));

        double quantidadeMoeda = valor / cotacaoPassada;
        double valorHoje = quantidadeMoeda * cotacaoAtual;

        double diferenca = valorHoje - valor;

        if (mes < 1 || mes > 12 || dia < 1 || dia > 31) {
            model.addAttribute("erro", "Data inválida.");
            return "logado :: simulacaor";
        }

        String resultado;
        if (diferenca > 0) {
            resultado = "Lucro de R$ " + String.format("%.2f", diferenca);
        } else if (diferenca < 0) {
            resultado = "Prejuízo de R$ " + String.format("%.2f", Math.abs(diferenca));
        } else {
            resultado = "Nem lucro nem prejuízo.";
        }

        model.addAttribute("cotacaoAtual", cotacaoAtual);
        model.addAttribute("cotacaoPassada", cotacaoPassada);
        model.addAttribute("valorConvertidoNoPassado", valor);
        model.addAttribute("valorHoje", valorHoje);
        model.addAttribute("resultado", resultado);

        return "logado :: simulacaor";
    }
}


