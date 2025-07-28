import javax.swing.*; //Biblioteca para desenvolver interfaces gráficas
import java.awt.*; //Layout e fontes
import java.awt.event.*; //Criação de eventos, click do mouse e botões

public class JogoGUI extends JFrame {
    private JButton[][] botoes; 
    private Tabuleiro tabuleiro; //lógica do jogo que é puxado do arquivo Tabuleiro.java
    private int bombas, linhas, tempoLimite, colunas; //COMO NÃO USA??? USA SIM PÔ/ informações do jogo
    private Timer timer;
    private int tempoRestante;
    private JLabel tempoLabel;

    public JogoGUI(int linhas, int colunas, int bombas, int tempoLimite) { //inicializa tudo que é essencial ao jogo
        this.linhas = linhas;
        this.colunas = colunas;
        this.bombas = bombas;                                     
        this.tempoLimite = tempoLimite;
        this.tempoRestante = tempoLimite;

        setTitle("Campo Minado");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        tabuleiro = new Tabuleiro(linhas, colunas, bombas);        //criação do tabuleiro e define os botões do campo
        botoes = new JButton[linhas][colunas];

        JPanel painel = new JPanel(new GridLayout(linhas, colunas));
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {                    //criação dos botões vazios
                JButton botao = new JButton("");
                final int l = i;
                final int c = j;
                botao.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 14));
                botao.setMargin(new Insets(0, 0, 0, 0));
                botao.addMouseListener(new MouseAdapter() {                       
                    public void mouseClicked(MouseEvent e) {
                        if (SwingUtilities.isRightMouseButton(e)) {         //definir botão direito para colocar bandeiras
                            if (botao.getText().equals("")) {
                                botao.setText("🚩");
                            } else if (botao.getText().equals("🚩")) {
                                botao.setText("");
                            }
                            return;
                        }

                        if (tabuleiro.isAberto(l, c)) {                   //verifica a quantidade de bombas ao redor do click e verifica se pode abrir uma área
                            int numero = Character.isDigit(botao.getText().charAt(0)) ? Integer.parseInt(botao.getText()) : -1;
                            if (numero >= 0) {
                                int bandeiras = contarBandeirasAoRedor(l, c);
                                if (bandeiras == numero) {
                                    tabuleiro.abrirVizinhas(l, c);
                                    atualizarTabuleiro();
                                    checarVitoria();
                                }
                            }
                            return;
                        }

                        if (tabuleiro.getPosicao(l, c) == -1) {            //verifica se o click foi numa bomba caso nao tenha sido o caso anterior e se for bomba o jogador perde
                            botao.setText("💣");
                            revelarMinas();
                            JOptionPane.showMessageDialog(null, "Você perdeu!");
                            mostrarOpcoes();
                        } else {
                            tabuleiro.abrirVizinhas(l, c);
                            atualizarTabuleiro();
                            checarVitoria();
                        }
                    }
                });
                botoes[i][j] = botao; //adiciona os botões ao painel
                painel.add(botao);
            }
        }

        add(painel, BorderLayout.CENTER);

        // Tempo formatado desde o início
        tempoLabel = new JLabel("Tempo restante: " + formatarTempo(tempoRestante));
        tempoLabel.setHorizontalAlignment(SwingConstants.CENTER);                  //coloca o painel e o temporizador na janela
        add(tempoLabel, BorderLayout.SOUTH);

        timer = new Timer(1000, e -> { //"e" não é usado mesmo, ta tudo certo! 
            tempoRestante--;
            tempoLabel.setText("Tempo restante: " + formatarTempo(tempoRestante));
            if (tempoRestante <= 0) {
                timer.stop();
                revelarMinas();
                JOptionPane.showMessageDialog(null, "Tempo esgotado! Você perdeu!");
                mostrarOpcoes();
            }
        });
        timer.start();

        setSize(colunas * 35, linhas * 35 + 50);      //define o tamanho da janela que será criada
        setLocationRelativeTo(null);
        setVisible(true);
    }

    // NOVO MÉTODO: Formata o tempo como mm:ss
    private String formatarTempo(int segundos) {
        int minutos = segundos / 60;                                 //formatando o tempo em mm:ss
        int segRestantes = segundos % 60;
        return String.format("%02d:%02d", minutos, segRestantes);
    }

    private int contarBandeirasAoRedor(int linha, int coluna) {                   //conta a quantidade de bandeiras ao redor dos botões
        int count = 0;
        for (int i = linha - 1; i <= linha + 1; i++) {
            for (int j = coluna - 1; j <= coluna + 1; j++) {
                if (i >= 0 && i < linhas && j >= 0 && j < colunas) {
                    if (botoes[i][j].getText().equals("🚩")) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    private void atualizarTabuleiro() {                     //atualiza os botões pensando no que foi aberto
        char[][] estado = tabuleiro.getTabuleiro();
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (estado[i][j] != '_') {
                    if (estado[i][j] == '*') {
                        botoes[i][j].setText("💣");
                    } else {
                        botoes[i][j].setText(Character.toString(estado[i][j]));
                    }
                    botoes[i][j].setEnabled(false);
                }
            }
        }
    }

    private void revelarMinas() {      //revela as minas e trava os botões
        tabuleiro.exibeMinas();
        atualizarTabuleiro();
        timer.stop();
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                botoes[i][j].setEnabled(false);
            }
        }
    }

    private void checarVitoria() {          //verifica se o jogador venceu, caso verdadeiro, revela as minas
        if (tabuleiro.ganhou()) {
            revelarMinas();
            JOptionPane.showMessageDialog(null, "Você venceu!");
            mostrarOpcoes();
        }
    }
 
    private void mostrarOpcoes() {                      //faz a pergunta de ir ao menu ou sair do jogo, após uma vitória ou derrota
        int resposta = JOptionPane.showOptionDialog(this,
                "O que deseja fazer?",
                "Fim de jogo",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                new String[]{"Menu", "Sair"},
                "Menu");

        if (resposta == JOptionPane.YES_OPTION) {
            dispose();
            criarMenu();
        } else {
            System.exit(0);
        }
    }

    public static void criarMenu() {                //faz a criação do menu inicial com as opções
        String[] opcoes = {
                "Fácil (8x10, 10 bombas, 5min)",
                "Médio (14x18, 30 bombas, 15min)",
                "Difícil (20x24, 99 bombas, 60min)"
        };

        String escolha = (String) JOptionPane.showInputDialog(
                null,
                "Escolha a dificuldade:",
                "Campo Minado - Menu",
                JOptionPane.QUESTION_MESSAGE,
                null,
                opcoes,
                opcoes[0]
        );

        if (escolha == null) System.exit(0);

        switch (escolha) {
            case "Fácil (8x10, 10 bombas, 5min)":
                new JogoGUI(8, 10, 10, 300);
                break;
            case "Médio (14x18, 30 bombas, 15min)":
                new JogoGUI(14, 18, 30, 900);
                break;
            case "Difícil (20x24, 99 bombas, 60min)":
                new JogoGUI(20, 24, 99, 3600);
                break;
            default:
                System.exit(0);
        }
    }

    public static void main(String[] args) {
        criarMenu();
    }
}
