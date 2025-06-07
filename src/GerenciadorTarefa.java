import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

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
    
    // Método para fazer parse correto do CSV com campos entre aspas
    private String[] parsearLinhaCSV(String linha) {
        List<String> campos = new ArrayList<>();
        Pattern pattern = Pattern.compile("\"([^\"]*)\"|([^,]+)");
        Matcher matcher = pattern.matcher(linha);
        
        while (matcher.find()) {
            if (matcher.group(1) != null) {
                // Campo entre aspas
                campos.add(matcher.group(1));
            } else {
                // Campo sem aspas
                campos.add(matcher.group(2));
            }
        }
        
        return campos.toArray(new String[0]);
    }
    
    private Tarefa criarTarefaAPartirDeLinha(String linha) {
        try {
            String[] partes = parsearLinhaCSV(linha);
            
            if (partes.length >= 6) { 
                int id = Integer.parseInt(partes[0].trim());
                String descricao = partes[1].trim();
                String statusStr = partes[2].trim();
                String usuario = partes[3].trim();
                String dataCriacao = partes[4].trim();
                String dataConclusao = partes[5].trim();

                // Validação do status
                Tarefa.Status status;
                try {
                    status = Tarefa.Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Status inválido encontrado: '" + statusStr + "'. Usando PENDENTE como padrão.");
                    status = Tarefa.Status.PENDENTE;
                }

                Tarefa tarefa = new Tarefa(descricao, usuario, status);
                tarefa.setId(id);
                
                tarefa.setDataCriacaoAPartirDeString(dataCriacao);
                tarefa.setDataConclusaoAPartirDeString(dataConclusao);
                
                return tarefa;
            } else if (partes.length >= 4) { // Compatibilidade com formato antigo
                int id = Integer.parseInt(partes[0].trim());
                String descricao = partes[1].trim();
                String statusStr = partes[2].trim();
                String usuario = partes[3].trim();

                // Validação do status
                Tarefa.Status status;
                try {
                    status = Tarefa.Status.valueOf(statusStr);
                } catch (IllegalArgumentException e) {
                    System.out.println("Status inválido encontrado: '" + statusStr + "'. Usando PENDENTE como padrão.");
                    status = Tarefa.Status.PENDENTE;
                }

                Tarefa tarefa = new Tarefa(descricao, usuario, status);
                tarefa.setId(id);
                return tarefa;
            }
        } catch (Exception e) {
            System.out.println("Erro ao processar linha do CSV: " + linha + " - " + e.getMessage());
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
            String linha = reader.readLine(); // Pula o cabeçalho
            if (linha == null) {
                return todasTarefas;
            }

            int maiorId = 0;
            int numeroLinha = 1;
            while ((linha = reader.readLine()) != null) {
                numeroLinha++;
                linha = linha.trim();
                
                if (linha.isEmpty()) {
                    continue;
                }
                
                Tarefa tarefa = criarTarefaAPartirDeLinha(linha);
                if (tarefa != null) {
                    todasTarefas.add(tarefa);
                    if (tarefa.getId() > maiorId) {
                        maiorId = tarefa.getId();
                    }
                } else {
                    System.out.println("Linha " + numeroLinha + " ignorada: formato inválido - " + linha);
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
    
    // Método para escapar campos que contêm vírgulas ou aspas
    private String escaparCampoCSV(String campo) {
        if (campo == null) {
            return "";
        }
        
        // Se o campo contém vírgula, quebra de linha ou aspas, precisa ser envolvido em aspas
        if (campo.contains(",") || campo.contains("\"") || campo.contains("\n") || campo.contains("\r")) {
            // Escapa aspas duplicando-as e envolve o campo em aspas
            return "\"" + campo.replace("\"", "\"\"") + "\"";
        }
        
        return campo;
    }
    
    private void salvarTarefasNoArquivo(List<Tarefa> tarefasParaSalvar) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_TAREFAS))) {
            writer.write("id,descricao,status,usuario,dataCriacao,dataConclusao");
            writer.newLine();
            
            for (Tarefa tarefa : tarefasParaSalvar) {
                String linha = String.format("%d,%s,%s,%s,%s,%s",
                        tarefa.getId(),
                        escaparCampoCSV(tarefa.getDescricao()),
                        tarefa.getStatus(),
                        escaparCampoCSV(tarefa.getUsuario()),
                        escaparCampoCSV(tarefa.getDataCriacaoFormatada()),
                        escaparCampoCSV(tarefa.getDataConclusaoFormatada()));
                writer.write(linha);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar tarefas: " + e.getMessage());
        }
    }
}