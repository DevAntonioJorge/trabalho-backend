import java.util.HashMap;
import java.util.Map;

public class GerenciadorUsuario {
    private Map<String, String> usuarios = new HashMap<>();

    public boolean adicionarUsuario(String nome, String senha) {
        if (usuarios.containsKey(nome)) {
            // Usuário já existe, não cadastra
            return false;
        }
        // Adiciona usuário novo
        usuarios.put(nome, senha);
        return true;
    }

    public boolean login(String nome, String senha) {
        return usuarios.containsKey(nome) && usuarios.get(nome).equals(senha);
    }
}
