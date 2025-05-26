import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Main {
    private static GerenciadorUsuario usuarios = new GerenciadorUsuario();
    private static GerenciadorTarefa gerenciadorTarefa = new GerenciadorTarefa();
    private static String usuarioLogado = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarTelaLogin);
    }

    private static void mostrarTelaLogin() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 180);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 5, 5));

        JTextField txtUsuario = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        painelCampos.add(new JLabel("Usuário:"));
        painelCampos.add(txtUsuario);
        painelCampos.add(new JLabel("Senha:"));
        painelCampos.add(txtSenha);

        frame.add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnCadastrar = new JButton("Cadastrar");

        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCadastrar);

        frame.add(painelBotoes, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {
            String nome = txtUsuario.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

            if (nome.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha usuário e senha.");
                return;
            }

            if (usuarios.login(nome, senha)) {
                usuarioLogado = nome;
                frame.dispose();
                mostrarTelaPrincipal();
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário ou senha incorretos.");
            }
        });

        btnCadastrar.addActionListener(e -> {
            String nome = txtUsuario.getText().trim();
            String senha = new String(txtSenha.getPassword()).trim();

            if (nome.isEmpty() || senha.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "Preencha usuário e senha para cadastrar.");
                return;
            }

            if (usuarios.adicionarUsuario(nome, senha)) {
                JOptionPane.showMessageDialog(frame, "Usuário cadastrado com sucesso!");
            } else {
                JOptionPane.showMessageDialog(frame, "Usuário já existe.");
            }
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void mostrarTelaPrincipal() {
        JFrame frame = new JFrame("ToDo List - Usuário: " + usuarioLogado);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        DefaultListModel<Tarefa> modeloLista = new DefaultListModel<>();
        JList<Tarefa> listaTarefas = new JList<>(modeloLista);
        listaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTarefas.setCellRenderer(new DefaultListCellRenderer() {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tarefa) {
                    Tarefa t = (Tarefa) value;
                    String dataCriacao = sdf.format(new Date(t.getDataCriacao()));
                    String dataConclusao = t.getDataConclusao() != "" ? sdf.format(new Date(t.getDataConclusao())) : "-";

                    String texto = String.format("<html><b>%s</b> | Criada: %s | Conclusão: %s | Status: %s</html>",
                            t.getDescricao(), dataCriacao, dataConclusao, t.getStatus());
                    setText(texto);
                }
                return c;
            }
        });

        JScrollPane scroll = new JScrollPane(listaTarefas);

        JPanel painelBotoes = new JPanel();

        JButton btnAdicionar = new JButton("Adicionar");
        JButton btnCompletar = new JButton("Completar");
        JButton btnRemover = new JButton("Remover");
        JButton btnLogout = new JButton("Logout");

        painelBotoes.add(btnAdicionar);
        painelBotoes.add(btnCompletar);
        painelBotoes.add(btnRemover);
        painelBotoes.add(btnLogout);

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(painelBotoes, BorderLayout.SOUTH);

        atualizarListaTarefas(modeloLista);

        btnAdicionar.addActionListener(e -> {
            String descricao = JOptionPane.showInputDialog(frame, "Descrição da tarefa:");
            if (descricao == null || descricao.trim().isEmpty()) return;

            long agora = System.currentTimeMillis();
            Tarefa novaTarefa = new Tarefa(descricao.trim(), usuarioLogado, Tarefa.Status.PENDENTE);
            gerenciadorTarefa.adicionarTarefa(novaTarefa);
            atualizarListaTarefas(modeloLista);
        });

        btnCompletar.addActionListener(e -> {
            Tarefa selecionada = listaTarefas.getSelectedValue();
            if (selecionada == null) {
                JOptionPane.showMessageDialog(frame, "Selecione uma tarefa para completar.");
                return;
            }
            if (!selecionada.getUsuario().equals(usuarioLogado)) {
                JOptionPane.showMessageDialog(frame, "Só pode completar tarefas do seu usuário.");
                return;
            }
            selecionada.setStatus(Tarefa.Status.CONCLUIDA);
            selecionada.setDataConclusao();
            atualizarListaTarefas(modeloLista);
        });

        btnRemover.addActionListener(e -> {
            Tarefa selecionada = listaTarefas.getSelectedValue();
            if (selecionada == null) {
                JOptionPane.showMessageDialog(frame, "Selecione uma tarefa para remover.");
                return;
            }
            if (!selecionada.getUsuario().equals(usuarioLogado)) {
                JOptionPane.showMessageDialog(frame, "Só pode remover tarefas do seu usuário.");
                return;
            }
            gerenciadorTarefa.removerTarefa(selecionada);
            atualizarListaTarefas(modeloLista);
        });

        btnLogout.addActionListener(e -> {
            usuarioLogado = null;
            frame.dispose();
            mostrarTelaLogin();
        });

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    private static void atualizarListaTarefas(DefaultListModel<Tarefa> modeloLista) {
        modeloLista.clear();
        for (Tarefa t : gerenciadorTarefa.listarTarefas()) {
            if (t.getUsuario().equals(usuarioLogado)) {
                modeloLista.addElement(t);
            }
        }
    }
}
