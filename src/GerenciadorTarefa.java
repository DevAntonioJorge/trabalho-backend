import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GerenciadorTarefa {
    private List<Tarefa> tarefas = new ArrayList<>();
    private final String ARQUIVO_TAREFAS = "tarefas.csv";
    private int proximoId = 1;

    public GerenciadorTarefa() {
        carregarTarefas();
    }    
    public void adicionarTarefa(Tarefa tarefa) {
        tarefa.setId(proximoId++);
        tarefas.add(tarefa);
        salvarTarefas();
    }    
    public void removerTarefa(Tarefa tarefa) {
        tarefas.remove(tarefa);
        salvarTarefas();
    }    
    public void completarTarefa(Tarefa tarefa) {
        tarefa.setStatus(Tarefa.Status.CONCLUIDA);
        salvarTarefas();
    }

    public List<Tarefa> listarTarefas() {
        return tarefas;
    }

    public Tarefa buscarTarefaPorId(int id) {
        for (Tarefa tarefa : tarefas) {
            if (tarefa.getId() == id) {
                return tarefa;
            }
        }
        return null;
    }

    private void criarArquivoTarefas() {
        File arquivo = new File(ARQUIVO_TAREFAS);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar arquivo de tarefas: " + e.getMessage());
            }
        }
    }    private void carregarTarefas() {
        File arquivo = new File(ARQUIVO_TAREFAS);
        if (!arquivo.exists()) {
            criarArquivoTarefas();
            return;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine(); // Pula cabeçalho CSV
            if (linha == null) {
                return; 
            }
            
            int maiorId = 0;
            while ((linha = reader.readLine()) != null) {
                String[] partes = linha.split(",");
                if (partes.length >= 5) {
                    int id = Integer.parseInt(partes[0]);
                    String descricao = partes[2];
                    Tarefa.Status status = Tarefa.Status.valueOf(partes[3]);
                    String usuario = partes[4];
                    
                    Tarefa tarefa = new Tarefa(descricao, usuario, status);
                    tarefa.setId(id);
                    tarefas.add(tarefa);
                    
                    if (id > maiorId) {
                        maiorId = id;
                    }
                }
            }
            
            proximoId = maiorId + 1;
            
        } catch (Exception e) {
            System.out.println("Erro ao carregar tarefas: " + e.getMessage());
        }
    }    public void salvarTarefas() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TAREFAS))) {
            writer.write("id,titulo,descricao,status,usuario");
            writer.newLine();
            
            for (Tarefa tarefa : tarefas) {
                String linha = String.format("%d,\"%s\",\"%s\",%s,\"%s\"", 
                    tarefa.getId(),
                    tarefa.getDescricao(),
                    tarefa.getStatus(),
                    tarefa.getUsuario());
                writer.write(linha);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }
}
