import java.util.ArrayList;
import java.util.List;

public class GerenciadorTarefa {
    private final List<Tarefa> tarefas = new ArrayList<>();

    public void adicionarTarefa(Tarefa tarefa) {
        tarefas.add(tarefa);
    }

    public void removerTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
    }

    public void completarTarefa(Tarefa tarefa) {
        tarefa.setStatus(Tarefa.Status.CONCLUIDA);
    }

    public List<Tarefa> listarTarefas() {
        return tarefas;
    }
}
