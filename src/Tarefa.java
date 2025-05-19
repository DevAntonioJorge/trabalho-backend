public class Tarefa {
    private int id;
    private String descricao;
    private int dataCriacao;
    private int dataConclusao;
    private String usuario;
    private Status status;

    public enum Status {
        EM_ANDAMENTO,
        CONCLUIDA,
        PENDENTE
    }

    public Tarefa(String descricao, int dataCriacao, int dataConclusao, String usuario, Status status) {
        this.descricao = descricao;
        this.dataCriacao = dataCriacao;
        this.dataConclusao = dataConclusao;
        this.usuario = usuario;
        this.status = status;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(int dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public int getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao(int dataConclusao) {
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
    public int getId() {return id;}
}

