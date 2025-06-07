import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class Main {
    private static final GerenciadorUsuario usuarios = new GerenciadorUsuario();
    private static final GerenciadorTarefa gerenciadorTarefa = new GerenciadorTarefa();
    private static String usuarioLogado = null;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::mostrarTelaLogin);
    }

    private static boolean validarCamposVazios(String nome, String senha, JFrame frame, String mensagemErro) {
        if (nome.isEmpty() || senha.isEmpty()) {
            JOptionPane.showMessageDialog(frame, mensagemErro);
            return true;
        }
        return false;
    }
    
    private static String[] obterCredenciais(JTextField txtUsuario, JPasswordField txtSenha) {
        String nome = txtUsuario.getText().trim();
        String senha = new String(txtSenha.getPassword()).trim();
        return new String[]{nome, senha};
    }

    private static void mostrarTelaLogin() {
        JFrame frame = new JFrame("Login");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(300, 180);
        frame.setLayout(new BorderLayout(10, 10));

        JPanel painelCampos = criarPainelCampos();
        JTextField txtUsuario = (JTextField) painelCampos.getComponent(1);
        JPasswordField txtSenha = (JPasswordField) painelCampos.getComponent(3);

        frame.add(painelCampos, BorderLayout.CENTER);

        JPanel painelBotoes = new JPanel();
        JButton btnLogin = new JButton("Login");
        JButton btnCadastrar = new JButton("Cadastrar");

        painelBotoes.add(btnLogin);
        painelBotoes.add(btnCadastrar);

        frame.add(painelBotoes, BorderLayout.SOUTH);

        btnLogin.addActionListener(e -> {
            String[] credenciais = obterCredenciais(txtUsuario, txtSenha);
            String nome = credenciais[0];
            String senha = credenciais[1];

            if (validarCamposVazios(nome, senha, frame, "Preencha usuário e senha.")) {
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
            String[] credenciais = obterCredenciais(txtUsuario, txtSenha);
            String nome = credenciais[0];
            String senha = credenciais[1];

            if (validarCamposVazios(nome, senha, frame, "Preencha usuário e senha para cadastrar.")) {
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
    
    private static JPanel criarPainelCampos() {
        JPanel painelCampos = new JPanel(new GridLayout(4, 1, 5, 5));
        JTextField txtUsuario = new JTextField();
        JPasswordField txtSenha = new JPasswordField();

        painelCampos.add(new JLabel("Usuário:"));
        painelCampos.add(txtUsuario);
        painelCampos.add(new JLabel("Senha:"));
        painelCampos.add(txtSenha);
        
        return painelCampos;
    }
    
    private static boolean validarTarefaSelecionada(Tarefa tarefa, JFrame frame, String acao) {
        if (tarefa == null) {
            JOptionPane.showMessageDialog(frame, "Selecione uma tarefa para " + acao + ".");
            return true;
        }
        if (!tarefa.getUsuario().equals(usuarioLogado)) {
            JOptionPane.showMessageDialog(frame, "Só pode " + acao + " tarefas do seu usuário.");
            return true;
        }
        return false;
    }

    private static void mostrarTelaPrincipal() {
        gerenciadorTarefa.recarregarTarefasDoUsuario(usuarioLogado);
        
        JFrame frame = new JFrame("ToDo List - Usuário: " + usuarioLogado);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 400);
        frame.setLayout(new BorderLayout());

        DefaultListModel<Tarefa> modeloLista = new DefaultListModel<>();
        JList<Tarefa> listaTarefas = getTarefaJList(modeloLista);

        JScrollPane scroll = new JScrollPane(listaTarefas);
        JPanel painelBotoes = criarPainelBotoes();

        frame.add(scroll, BorderLayout.CENTER);
        frame.add(painelBotoes, BorderLayout.SOUTH);

        atualizarListaTarefas(modeloLista);
        
        configurarAcoesBotoes(frame, listaTarefas, modeloLista, painelBotoes);

        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    private static JPanel criarPainelBotoes() {
        JPanel painelBotoes = new JPanel();
        painelBotoes.add(new JButton("Adicionar"));
        painelBotoes.add(new JButton("Completar"));
        painelBotoes.add(new JButton("Remover"));
        painelBotoes.add(new JButton("Logout"));
        return painelBotoes;
    }
    
    private static void configurarAcoesBotoes(JFrame frame, JList<Tarefa> listaTarefas,
                                            DefaultListModel<Tarefa> modeloLista, JPanel painelBotoes) {
        
        JButton btnAdicionar = (JButton) painelBotoes.getComponent(0);
        JButton btnCompletar = (JButton) painelBotoes.getComponent(1);
        JButton btnRemover = (JButton) painelBotoes.getComponent(2);
        JButton btnLogout = (JButton) painelBotoes.getComponent(3);

        btnAdicionar.addActionListener(e -> {
            String descricao = JOptionPane.showInputDialog(frame, "Descrição da tarefa:");
            if (descricao == null || descricao.trim().isEmpty()) return;

            Tarefa novaTarefa = new Tarefa(descricao.trim(), usuarioLogado, Tarefa.Status.PENDENTE);
            gerenciadorTarefa.adicionarTarefa(novaTarefa);
            atualizarListaTarefas(modeloLista);
        });

        btnCompletar.addActionListener(e -> {
            Tarefa selecionada = listaTarefas.getSelectedValue();
            if (validarTarefaSelecionada(selecionada, frame, "completar")) return;
            
            gerenciadorTarefa.completarTarefa(selecionada);
            selecionada.setDataConclusao();
            atualizarListaTarefas(modeloLista);
        });

        btnRemover.addActionListener(e -> {
            Tarefa selecionada = listaTarefas.getSelectedValue();
            if (validarTarefaSelecionada(selecionada, frame, "remover")) return;
            
            gerenciadorTarefa.removerTarefa(selecionada);
            atualizarListaTarefas(modeloLista);
        });

        btnLogout.addActionListener(e -> {
            usuarioLogado = null;
            frame.dispose();
            mostrarTelaLogin();
        });
    }

    private static JList<Tarefa> getTarefaJList(DefaultListModel<Tarefa> modeloLista) {
        JList<Tarefa> listaTarefas = new JList<>(modeloLista);
        listaTarefas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaTarefas.setCellRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus) {
                Component c = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Tarefa t) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dataCriacao = t.getDataCriacao().format(formatter);
                    String dataConclusao = t.getDataConclusao() != null ?
                             t.getDataConclusao().format(formatter) : "-";

                    String texto = String.format("<html><b>%s</b> | Criada: %s | Conclusão: %s | Status: %s</html>",
                            t.getDescricao(), dataCriacao, dataConclusao, t.getStatus());
                    setText(texto);
                }
                return c;
            }
        });
        return listaTarefas;
    }

    private static void atualizarListaTarefas(DefaultListModel<Tarefa> modeloLista) {
        modeloLista.clear();
        for (Tarefa t : gerenciadorTarefa.listarTarefas()) {
            modeloLista.addElement(t);
        }
    }
}