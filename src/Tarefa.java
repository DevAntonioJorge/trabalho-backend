public class Tarefa {
    public enum Status {
        PENDENTE,
        CONCLUIDA
    }

    private String descricao;
    private long dataCriacao;
    private long dataConclusao;
    private String usuario;
    private Status status;

    public Tarefa(String descricao, long dataCriacao, long dataConclusao, String usuario, Status status) {
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.dataConclusao = dataConclusao;
        this.usuario = usuario;
        this.status = status;
    }

    // Getters e Setters
    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public long getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(long dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public long getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(long dataConclusao) {
        this.dataConclusao = dataConclusao;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return descricao;
    }
}
