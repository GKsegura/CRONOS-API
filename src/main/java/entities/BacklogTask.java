package entities;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BacklogTask {
    private Long id;
    private String descricao;
    private Categoria categoria;
    private String cliente;
    private Long duracaoMin;
    private String obs;
    private int prioridade;
    private String dataCriacao;
    private String dataLimite;

    private static final DateTimeFormatter FMT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    public BacklogTask() {}

    public BacklogTask(String descricao, Categoria categoria, String cliente,
                       Long duracaoMin, String obs, int prioridade, String dataLimite) {
        this.descricao = descricao;
        this.categoria = categoria;
        this.cliente = cliente;
        this.duracaoMin = duracaoMin;
        this.obs = obs;
        this.prioridade = prioridade;
        this.dataLimite = dataLimite;
        this.dataCriacao = LocalDate.now().format(FMT_DATA);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public Categoria getCategoria() { return categoria; }
    public void setCategoria(Categoria categoria) { this.categoria = categoria; }

    public String getCliente() { return cliente; }
    public void setCliente(String cliente) { this.cliente = cliente; }

    public Long getDuracaoMin() { return duracaoMin; }
    public void setDuracaoMin(Long duracaoMin) { this.duracaoMin = duracaoMin; }

    public String getObs() { return obs; }
    public void setObs(String obs) { this.obs = obs; }

    public int getPrioridade() { return prioridade; }
    public void setPrioridade(int prioridade) { this.prioridade = prioridade; }

    public String getDataCriacao() { return dataCriacao; }
    public void setDataCriacao(String dataCriacao) { this.dataCriacao = dataCriacao; }

    public String getDataLimite() { return dataLimite; }
    public void setDataLimite(String dataLimite) { this.dataLimite = dataLimite; }
}