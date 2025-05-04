import java.util.HashMap;
import java.util.Map;

public class GerenciadorUsuario {
    private Map<String, Usuario> usuarios = new HashMap<>();

    public void adicionarUsuario(String nome, String senha) {
        if (!usuarios.containsKey(nome)) {
            usuarios.put(nome, new Usuario(nome, senha));
        }
    }

    public boolean login(String nome, String senha) {
        Usuario usuario = usuarios.get(nome);
        return usuario != null && usuario.getSenha().equals(senha);
    }
}
