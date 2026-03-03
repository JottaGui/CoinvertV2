 package projeto_integrado.Entidades;


import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import projeto_integrado.enums.Estatus;
import projeto_integrado.enums.TipoOP;

import java.math.BigDecimal;
import java.util.Date;

@Entity
public class OpCambio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "id")
    private User user;

    @Enumerated(EnumType.STRING)
    private TipoOP operacao;

    @NotBlank
    private String moeda;


    @Digits(integer = 18, fraction = 2)
    private BigDecimal valor;


    @Digits(integer = 18, fraction = 8)
    private BigDecimal cotacao;


    @NotNull
    @Digits(integer = 18, fraction = 8)
    private BigDecimal quantidademoeda;


    private String mpPreferenceId;

    private String mpInitPoint;

    private String mpPaymentId;

    @Enumerated(EnumType.STRING)
    private Estatus estatus;


    @Temporal(TemporalType.TIMESTAMP)
    @CreationTimestamp
    private Date data_op;

    public Long  getId() {
        return id;
    }

    public void setId(Long  id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public TipoOP getOperacao() {
        return operacao;
    }

    public void setOperacao(TipoOP operacao) {
        this.operacao = operacao;
    }

    public String getMoeda() {
        return moeda;
    }

    public void setMoeda(String moeda) {
        this.moeda = moeda;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public BigDecimal getCotacao() {
        return cotacao;
    }

    public void setCotacao(BigDecimal cotacao) {
        this.cotacao = cotacao;
    }

    public BigDecimal getQuantidademoeda() {
        return quantidademoeda;
    }

    public void setQuantidademoeda(BigDecimal quantidademoeda) {
        this.quantidademoeda = quantidademoeda;
    }

    public String getMpPreferenceId() {
        return mpPreferenceId;
    }

    public void setMpPreferenceId(String mpPreferenceId) {
        this.mpPreferenceId = mpPreferenceId;
    }

    public String getMpInitPoint() {
        return mpInitPoint;
    }

    public void setMpInitPoint(String mpInitPoint) {
        this.mpInitPoint = mpInitPoint;
    }

    public String getMpPaymentId() {
        return mpPaymentId;
    }

    public void setMpPaymentId(String mpPaymentId) {
        this.mpPaymentId = mpPaymentId;
    }

    public Estatus getEstatus() {
        return estatus;
    }

    public void setEstatus(Estatus estatus) {
        this.estatus = estatus;
    }

    public Date getData_op() {
        return data_op;
    }

    public void setData_op(Date data_op) {
        this.data_op = data_op;
    }
}
