import java.time.LocalDateTime; 

public enum Status {
        PENDENTE,
        CONCLUIDA
    }

public class Tarefa {
    

    private int id;
    private String descricao;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataConclusao;
    private String usuario;
    private Status status;

    public Tarefa(String descricao, String usuario, Status status) {
        this.descricao = descricao;
        this.dataCriacao = LocalDateTime.now();
        this.dataConclusao = null;
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

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;
    }

    public void setDataConclusao() {
        this.dataConclusao = LocalDateTime.now();
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
