import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GerenciadorTarefa {
    private final List<Tarefa> tarefas = new ArrayList<>();
    private final String ARQUIVO_TAREFAS = "tarefas.csv";
    private int proximoId = 1;

    public GerenciadorTarefa() {
        carregarTarefas();
    }
    
    public void recarregarTarefasDoUsuario(String usuario) {
        tarefas.clear();
        carregarTarefasDoUsuario(usuario);
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
        tarefa.setDataConclusao(); // Define a data de conclusão
        salvarTarefas();
    }

    public List<Tarefa> listarTarefas() {
        return tarefas;
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
    }
    
    
    private Tarefa criarTarefaAPartirDeLinha(String linha) {
        String[] partes = linha.split(",");
        if (partes.length >= 6) { 
            int id = Integer.parseInt(partes[0]);
            String descricao = partes[1].replace("\"", "");
            Tarefa.Status status = Tarefa.Status.valueOf(partes[2]);
            String usuario = partes[3].replace("\"", "");
            String dataCriacao = partes[4].replace("\"", "");
            String dataConclusao = partes[5].replace("\"", "");

            Tarefa tarefa = new Tarefa(descricao, usuario, status);
            tarefa.setId(id);
            
            
            tarefa.setDataCriacaoAPartirDeString(dataCriacao);
            tarefa.setDataConclusaoAPartirDeString(dataConclusao);
            
            return tarefa;
        } else if (partes.length >= 4) { 
            int id = Integer.parseInt(partes[0]);
            String descricao = partes[1].replace("\"", "");
            Tarefa.Status status = Tarefa.Status.valueOf(partes[2]);
            String usuario = partes[3].replace("\"", "");

            Tarefa tarefa = new Tarefa(descricao, usuario, status);
            tarefa.setId(id);
            return tarefa;
        }
        return null;
    }
    
    private List<Tarefa> lerTodasTarefasDoArquivo() {
        List<Tarefa> todasTarefas = new ArrayList<>();
        File arquivo = new File(ARQUIVO_TAREFAS);
        
        if (!arquivo.exists()) {
            criarArquivoTarefas();
            return todasTarefas;
        }
        
        try (BufferedReader reader = new BufferedReader(new FileReader(arquivo))) {
            String linha = reader.readLine();
            if (linha == null) {
                return todasTarefas;
            }

            int maiorId = 0;
            while ((linha = reader.readLine()) != null) {
                Tarefa tarefa = criarTarefaAPartirDeLinha(linha);
                if (tarefa != null) {
                    todasTarefas.add(tarefa);
                    if (tarefa.getId() > maiorId) {
                        maiorId = tarefa.getId();
                    }
                }
            }
            
            proximoId = maiorId + 1;
            
        } catch (Exception e) {
            System.out.println("Erro ao carregar tarefas: " + e.getMessage());
        }
        
        return todasTarefas;
    }
    
    private void carregarTarefas() {
        tarefas.addAll(lerTodasTarefasDoArquivo());
    }
    
    private void carregarTarefasDoUsuario(String usuarioLogado) {
        List<Tarefa> todasTarefas = lerTodasTarefasDoArquivo();
        
        for (Tarefa tarefa : todasTarefas) {
            if (tarefa.getUsuario().equals(usuarioLogado)) {
                tarefas.add(tarefa);
            }
        }
    }

    public void salvarTarefas() {
        List<Tarefa> todasAsTarefas = lerTodasTarefasDoArquivo();
        
        String usuarioAtual = tarefas.isEmpty() ? null : tarefas.get(0).getUsuario();
        if (usuarioAtual != null) {
            todasAsTarefas.removeIf(t -> t.getUsuario().equals(usuarioAtual));
        }
        
        todasAsTarefas.addAll(tarefas);
        
        salvarTarefasNoArquivo(todasAsTarefas);
    }
    
    
    private void salvarTarefasNoArquivo(List<Tarefa> tarefasParaSalvar) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TAREFAS))) {
            writer.write("id,descricao,status,usuario,dataCriacao,dataConclusao");
            writer.newLine();
            
            for (Tarefa tarefa : tarefasParaSalvar) {
                String linha = String.format("%d,\"%s\",%s,\"%s\",\"%s\",\"%s\"",
                        tarefa.getId(),
                        tarefa.getDescricao(),
                        tarefa.getStatus(),
                        tarefa.getUsuario(),
                        tarefa.getDataCriacaoFormatada(),
                        tarefa.getDataConclusaoFormatada());
                writer.write(linha);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }
}
