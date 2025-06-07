import java.time.LocalDateTime; 
import java.time.format.DateTimeFormatter;

public class Tarefa {

    public enum Status {
    PENDENTE,
    CONCLUIDA
}

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
    
    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataConclusao() {
        return dataConclusao;  // Pode ser null
    }

    public void setDataConclusao() {
        this.dataConclusao = LocalDateTime.now();
    }
    
    public void setDataConclusao(LocalDateTime dataConclusao) {
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
    
    public void setId(int i) {this.id = i;}

    public int getId() {return id;}
    
    public String getDataCriacaoFormatada() {
        return dataCriacao != null ? dataCriacao.format(FORMATTER) : "";
    }
    
    public String getDataConclusaoFormatada() {
        return dataConclusao != null ? dataConclusao.format(FORMATTER) : "";
    }
    
    public void setDataCriacaoAPartirDeString(String dataStr) {
        if (dataStr != null && !dataStr.trim().isEmpty()) {
            this.dataCriacao = LocalDateTime.parse(dataStr, FORMATTER);
        }
    }
    
    public void setDataConclusaoAPartirDeString(String dataStr) {
        if (dataStr != null && !dataStr.trim().isEmpty()) {
            this.dataConclusao = LocalDateTime.parse(dataStr, FORMATTER);
        }
    }
    
    @Override
    public String toString() {
        return descricao;
    }
}