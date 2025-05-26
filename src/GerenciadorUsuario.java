import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GerenciadorUsuario {
    private Map<String, Usuario> usuarios = new HashMap<>();
    private final String ARQUIVO_USUARIOS = "usuarios.csv";
    private int proximoId = 1;

    public GerenciadorUsuario() {
        carregarUsuarios();
    }  
    public void adicionarUsuario(String nome, String senha) {
        if (!usuarios.containsKey(nome)) {
            Usuario usuario = new Usuario(proximoId++, nome, senha);
            usuarios.put(nome, usuario);
            salvarUsuarios();
        }
        // Adiciona usuário novo
        usuarios.put(nome, senha);
        return true;
    }

    public boolean login(String nome, String senha) {
        return usuarios.containsKey(nome) && usuarios.get(nome).equals(senha);
    }

    public Usuario buscarUsuarioPorId(int id) {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getId() == id) {
                return usuario;
            }
        }
        return null;
    }

    public Usuario buscarUsuarioPorNome(String nome) {
        return usuarios.get(nome);
    }

    public Map<String, Usuario> listarUsuarios() {
        return usuarios;
    }

    public int getProximoId() {
        return proximoId;
    }

    private void criarArquivoUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            try {
                arquivo.createNewFile();
            } catch (IOException e) {
                System.out.println("Erro ao criar arquivo de usuários: " + e.getMessage());
            }
        }
    }

    private void carregarUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            criarArquivoUsuarios();
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
                if (partes.length >= 3) {
                    int id = Integer.parseInt(partes[0]);
                    String nome = partes[1].replace("\"", ""); 
                    String senha = partes[2].replace("\"", ""); 
                    
                    Usuario usuario = new Usuario(id, nome, senha);
                    usuarios.put(nome, usuario);
                    
                    if (id > maiorId) {
                        maiorId = id;
                    }
                }
            }
            
            proximoId = maiorId + 1;
            
        } catch (Exception e) {
            System.out.println("Erro ao carregar usuários: " + e.getMessage());
        }
    }

    public void salvarUsuarios() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARQUIVO_USUARIOS))) {
            writer.write("id,nome,senha");
            writer.newLine();
            
            for (Usuario usuario : usuarios.values()) {
                String linha = String.format("%d,\"%s\",\"%s\"", 
                    usuario.getId(),
                    usuario.getNome(),
                    usuario.getSenha());
                writer.write(linha);
                writer.newLine();
            }
        } catch (Exception e) {
            System.out.println("Erro ao salvar usuários: " + e.getMessage());
        }
    }
}
