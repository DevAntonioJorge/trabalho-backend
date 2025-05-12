import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner myScanner = new Scanner(System.in);
        GerenciadorUsuario usuarios = new GerenciadorUsuario();
        GerenciadorTarefa tarefas = new GerenciadorTarefa();
        String logado = null;

        while (true) {
            System.out.println("\n1. Cadastrar");
            System.out.println("2. Login");
            System.out.println("3. Sair");
            System.out.print("Opção: ");

            int op = myScanner.nextInt();
            myScanner.nextLine();
            switch (op) {
                case 1 -> {
                    System.out.print("Usuário: ");
                    String nome = myScanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = myScanner.nextLine();
                    usuarios.adicionarUsuario(nome, senha);
                    System.out.println("Cadastrado");
                }
                case 2 -> {
                    System.out.print("Usuário: ");
                    String nome = myScanner.nextLine();
                    System.out.print("Senha: ");
                    String senha = myScanner.nextLine();
                    if (usuarios.login(nome, senha)) {
                        logado = nome;
                        System.out.println("Logado");
                    } else {
                        System.out.println("Login incorreto.");
                    }
                }
                case 3 -> {
                    System.out.println("Finalizando");
                    myScanner.close();
                    return;
                }
                default -> throw new AssertionError();
            }
        }
    }
}
