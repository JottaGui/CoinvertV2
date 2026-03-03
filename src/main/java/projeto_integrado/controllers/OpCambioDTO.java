package projeto_integrado.controllers;

import java.math.BigDecimal;

public class OpCambioDTO {


    private String moeda;
    private BigDecimal quantidadeMoeda;

    public BigDecimal getQuantidadeMoeda() {
        return quantidadeMoeda;
    }

    public void setQuantidadeMoeda(BigDecimal quantidadeMoeda) {
        this.quantidadeMoeda = quantidadeMoeda;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }
}
