package entities;

public class Task implements Comparable<Task> {
    private Long id;
    private String descricao;
    private Categoria categoria;
    private String cliente;
    private Long duracaoMin;
    private String obs;
    private boolean apontado;

    public Task() {
    }

    public Task(String descricao, Categoria categoria, String cliente, Long duracaoMin, String obs, boolean apontado) {
        this.descricao = descricao;
        this.categoria = categoria;
        this.cliente = cliente;
        this.duracaoMin = duracaoMin;
        this.obs = obs;
        this.apontado = apontado;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescricao() {
        return this.descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Categoria getCategoria() {
        return categoria;
    }

    public void setCategoria(Categoria categoria) {
        this.categoria = categoria;
    }

    public String getCliente() {
        return cliente;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public Long getDuracaoMin() {
        return duracaoMin;
    }

    public void setDuracaoMin(Long duracaoMin) {
        this.duracaoMin = duracaoMin;
    }

    public String getObs() {
        return obs;
    }

    public void setObs(String obs) {
        this.obs = obs;
    }

    // Getter adicional para compatibilidade com Gson
    public boolean getApontado() {
        return apontado;
    }

    // Getter padrão para boolean
    public boolean isApontado() {
        return apontado;
    }

    public void setApontado(boolean apontado) {
        this.apontado = apontado;
    }

    @Override
    public String toString() {
        String cat = (categoria != null ? categoria.toString() : "Sem categoria");
        String cli = (cliente != null && !cliente.isEmpty() ? cliente : "N/A");

        String base = descricao + " [" + cat + "] [" + cli + "]";

        if (duracaoMin != null) {
            base += " (" + formatarDuracao(duracaoMin) + ")";
        }

        return base;
    }

    private String formatarDuracao(Long minutos) {
        long h = minutos / 60;
        long m = minutos % 60;
        return String.format("%02dh%02dm", h, m);
    }

    @Override
    public int compareTo(Task outra) {
        String desc1 = this.descricao.trim();
        String desc2 = outra.descricao.trim();
        return desc1.compareToIgnoreCase(desc2);
    }
}