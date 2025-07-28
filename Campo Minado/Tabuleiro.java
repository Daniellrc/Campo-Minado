import java.util.Random; //para randomizar as bombas

public class Tabuleiro {
    private int[][] minas;
    private char[][] tabuleiro;
    private int linhas, colunas, bombas;
    private Random random = new Random();

    public Tabuleiro(int linhas, int colunas, int bombas) {      //iniciliza as matrizes, zera as casas, randomiza as bombas, númera as casas com as dicas
        this.linhas = linhas;
        this.colunas = colunas;
        this.bombas = bombas;
        minas = new int[linhas][colunas];
        tabuleiro = new char[linhas][colunas];
        iniciaMinas();
        sorteiaMinas();
        preencheDicas();
        iniciaTabuleiro();
    }

    public boolean ganhou() {             //verificador de vitória(todas as casas abertas)
        int count = 0;
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                if (tabuleiro[i][j] == '_')
                    count++;
        return count == bombas;
    }

    // Abre as casas ao redor da posição (linha, coluna), incluindo a própria
    public void abrirVizinhas(int linha, int coluna) {
        if (linha < 0 || linha >= linhas || coluna < 0 || coluna >= colunas) return;
        if (tabuleiro[linha][coluna] != '_') return;

        tabuleiro[linha][coluna] = Character.forDigit(minas[linha][coluna], 10);

        if (minas[linha][coluna] == 0) {
            for (int i = -1; i <= 1; i++) {
                for (int j = -1; j <= 1; j++) {
                    int l = linha + i;
                    int c = coluna + j;
                    if (l >= 0 && l < linhas && c >= 0 && c < colunas) {
                        abrirVizinhas(l, c);
                    }
                }
            }
        }
    }

    public int getPosicao(int linha, int coluna) {     //mostra o valor da celula(bomba ou número)
        return minas[linha][coluna]; 
    }

    public char[][] getTabuleiro() {
        return tabuleiro;
    }
    public boolean isAberto(int linha, int coluna) {
    return tabuleiro[linha][coluna] != '_';      //retorna que a celula já foi aberta caso for diferente de _
}


    public void exibeMinas() {                 //revala as bombas, trocando o _ por *
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (minas[i][j] == -1) {
                    tabuleiro[i][j] = '*';
                }
            }
        }
    }

    private void iniciaTabuleiro() {            //deixa todas as casas com _
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                tabuleiro[i][j] = '_';
    }
 
    private void iniciaMinas() {                    //zera as minas
        for (int i = 0; i < linhas; i++)
            for (int j = 0; j < colunas; j++)
                minas[i][j] = 0;
    }

    private void sorteiaMinas() {        //sorteia as posições da bomba
        int minasColocadas = 0;
        while (minasColocadas < bombas) {
            int linha = random.nextInt(linhas);
            int coluna = random.nextInt(colunas);
            if (minas[linha][coluna] != -1) {
                minas[linha][coluna] = -1;
                minasColocadas++;
            }
        }
    }

    private void preencheDicas() {             //conta quantas bombas tem envolta de determinada casa
        for (int i = 0; i < linhas; i++) {
            for (int j = 0; j < colunas; j++) {
                if (minas[i][j] == -1) continue;
                for (int di = -1; di <= 1; di++) {
                    for (int dj = -1; dj <= 1; dj++) {
                        int ni = i + di;
                        int nj = j + dj;
                        if (ni >= 0 && ni < linhas && nj >= 0 && nj < colunas && minas[ni][nj] == -1) {
                            minas[i][j]++;
                        }
                    }
                }
            }
        }
    }
}
