package entidade;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "ordem_servico")
public class ordemServico implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Número da ordem gerado automaticamente (ex: OS-00001)
    @Column(nullable = false, unique = true, length = 20)
    private String numeroOrdem;

    // Relacionamento com cliente
    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private cliente cliente;

    // Relacionamento com o funcionário que abriu a ordem
    @ManyToOne
    @JoinColumn(name = "usuario_id", nullable = false)
    private usuario funcionario;

    // Equipamento
    @NotNull(message = "O equipamento é obrigatório")
    @Size(max = 100, message = "Equipamento deve ter no máximo 100 caracteres")
    @Column(nullable = false, length = 100)
    private String equipamento;

    @Size(max = 50, message = "Marca deve ter no máximo 50 caracteres")
    @Column(length = 50)
    private String marca;

    @Size(max = 100, message = "Modelo deve ter no máximo 100 caracteres")
    @Column(length = 100)
    private String modelo;

    @Size(max = 100, message = "Número de série deve ter no máximo 100 caracteres")
    @Column(name = "numero_serie", length = 100)
    private String numeroSerie;

    // Defeito relatado
    @NotNull(message = "O defeito é obrigatório")
    @Size(max = 500, message = "Defeito deve ter no máximo 500 caracteres")
    @Column(nullable = false, length = 500)
    private String defeito;

    // Observações internas
    @Size(max = 500, message = "Observações deve ter no máximo 500 caracteres")
    @Column(length = 500)
    private String observacoes;

    // Status: AGUARDANDO_ANALISE, AGUARDANDO_PECA, PRE_PRONTO, CONCLUIDA, CANCELADA
    @Column(nullable = false, length = 30)
    private String status = "AGUARDANDO_ANALISE";

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date dataAbertura;

    @Temporal(TemporalType.TIMESTAMP)
    @Column
    private Date dataConclusao;

    @PrePersist
    public void prePersist() {
        dataAbertura = new Date();
    }

    // Getters e Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getNumeroOrdem() { return numeroOrdem; }
    public void setNumeroOrdem(String numeroOrdem) { this.numeroOrdem = numeroOrdem; }

    public cliente getCliente() { return cliente; }
    public void setCliente(cliente cliente) { this.cliente = cliente; }

    public usuario getFuncionario() { return funcionario; }
    public void setFuncionario(usuario funcionario) { this.funcionario = funcionario; }

    public String getEquipamento() { return equipamento; }
    public void setEquipamento(String equipamento) { this.equipamento = equipamento; }

    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }

    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }

    public String getNumeroSerie() { return numeroSerie; }
    public void setNumeroSerie(String numeroSerie) { this.numeroSerie = numeroSerie; }

    public String getDefeito() { return defeito; }
    public void setDefeito(String defeito) { this.defeito = defeito; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(Date dataAbertura) { this.dataAbertura = dataAbertura; }

    public Date getDataConclusao() { return dataConclusao; }
    public void setDataConclusao(Date dataConclusao) { this.dataConclusao = dataConclusao; }
}