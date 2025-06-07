import java.util.HashMap;
import java.util.Map;
import java.io.File;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class GerenciadorUsuario {
    private final Map<Integer, Usuario> usuarios = new HashMap<>();
    private final String ARQUIVO_USUARIOS = "usuarios.csv";
    private int proximoId = 1;

    public GerenciadorUsuario() {
        carregarUsuarios();
    }  
    public boolean adicionarUsuario(String nome, String senha) {
        if (buscarUsuarioPorNome(nome) != null) {
            return false;
        }
        Usuario usuario = new Usuario(proximoId++, nome, senha);
        usuarios.put(usuario.getId(), usuario);
        salvarUsuarios();
        return true;
    }

    public boolean login(String nome, String senha) {
        Usuario usuario = buscarUsuarioPorNome(nome);
        return usuario != null && usuario.getSenha().equals(senha);
    }

    public Usuario buscarUsuarioPorNome(String nome) {
        for (Usuario usuario : usuarios.values()) {
            if (usuario.getNome().equals(nome)) {
                return usuario;
            }
        }
        return null;
    }

    private void criarArquivoUsuarios() {
        File arquivo = new File(ARQUIVO_USUARIOS);
        if (!arquivo.exists()) {
            try {
                if (!arquivo.createNewFile()) {
                    throw new IOException();
                }
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
            String linha = reader.readLine();

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
                    usuarios.put(id, usuario);
                    
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
