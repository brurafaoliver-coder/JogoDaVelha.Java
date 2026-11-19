package jogador;

import java.io.*;

public class JogadorHumano implements Jogador
{
    private Marca          marca     = null;
    private Tabuleiro      tabDoJogo = null;
    // Um unico leitor evita perder entradas ja armazenadas entre partidas.
    private static final BufferedReader teclado =
        new BufferedReader (new InputStreamReader (System.in));

    public JogadorHumano (Tabuleiro tab) throws Exception
    {
        // atribuir tab para tabDoJogo
        // pedir para o jogador escolher
        // sua marca e permitir digitacao
        // instanciando marca
        if (tab == null)
            throw new Exception("Tabuleiro ausente");

        this.tabDoJogo = tab;

        char smb = ' ';
        while (smb != 'X' && smb != 'O')
        {
            System.out.print("Escolha sua marca (X ou O): ");
            String digitado = teclado.readLine();
            if (digitado == null)
                throw new EOFException("Entrada encerrada durante a escolha da marca.");

            digitado = digitado.trim().toUpperCase();
            if (digitado.length() == 1 &&
                (digitado.charAt(0) == 'X' || digitado.charAt(0) == 'O'))
                smb = digitado.charAt(0);
            else
                System.out.println("Opcao invalida! Digite apenas X ou O.");
        }

        this.marca = new Marca(smb);
    }

    public JogadorHumano (Tabuleiro tab, Marca mrc) throws Exception
    {
        // atribuir tab para tabDoJogo
        // e mrc para marca
        if (tab == null)
            throw new Exception("Tabuleiro ausente");
        if (mrc == null)
            throw new Exception("Marca ausente");

        this.tabDoJogo = tab;
        this.marca = (Marca) mrc.clone();
    }

    @Override
    public Marca getMarca ()
    {
        // retorna a marca do jogador
        return (Marca) this.marca.clone();
    }

    @Override
    public void facaSuaJogada ()
    {
        // pergunta para o jogador onde ele quer
        // jogar, permitindo digitacao e chamando
        // os métodos apropriados de tabDoJogo
        System.out.println();
        System.out.println("Sua vez de jogar! Sua marca e: " + this.marca);
        System.out.println("Tabuleiro atual:");
        System.out.println(this.tabDoJogo);

        while (true)
        {
            try
            {
                System.out.print("Digite a linha onde deseja jogar (1, 2 ou 3): ");
                String linStr = teclado.readLine();
                if (linStr == null)
                    throw new EOFException("Entrada encerrada durante a jogada.");

                System.out.print("Digite a coluna onde deseja jogar (1, 2 ou 3): ");
                String colStr = teclado.readLine();
                if (colStr == null)
                    throw new EOFException("Entrada encerrada durante a jogada.");

                int lin = Integer.parseInt(linStr.trim());
                int col = Integer.parseInt(colStr.trim());

                if (lin < 1 || lin > 3 || col < 1 || col > 3)
                {
                    System.out.println("Posicao invalida! Linha e coluna devem ser 1, 2 ou 3.");
                    continue;
                }

                // A interface usa 1 a 3; os metodos do tabuleiro usam 0 a 2.
                lin--;
                col--;

                if (this.tabDoJogo.haMarcaNaPosicao(lin, col))
                {
                    System.out.println("Essa posicao ja esta ocupada! Escolha outra.");
                    continue;
                }

                this.tabDoJogo.setMarcaNaPosicao(this.marca, lin, col);
                System.out.println("\nJogada realizada com sucesso:");
                System.out.println(this.tabDoJogo);
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Entrada invalida! Digite apenas numeros inteiros (1, 2 ou 3).");
            }
            catch (IOException erro)
            {
                // EOF cancela a sessao; falhas de leitura tambem chegam ao controle.
                throw new UncheckedIOException("Nao foi possivel ler a jogada.", erro);
            }
            catch (Exception erro)
            {
                throw new IllegalStateException("Nao foi possivel registrar a jogada humana.", erro);
            }
        }
    }

    public void vcGanhou ()
    {
        // mostra mensagem indicando vitoria
        System.out.println("\n==================================");
        System.out.println("PARABENS! VOCE VENCEU! :)");
        System.out.println("==================================");
        System.out.println(this.tabDoJogo);
    }

    public void vcPerdeu ()
    {
        // mostra mensagem indicando derrota
        System.out.println("\n==================================");
        System.out.println("QUE PENA! VOCE PERDEU! :(");
        System.out.println("==================================");
        System.out.println(this.tabDoJogo);
    }

    public void deuVelha ()
    {
        // mostra mensagem indicando empate
        System.out.println("\n==================================");
        System.out.println("DEU VELHA! PARTIDA EMPATADA!");
        System.out.println("==================================");
        System.out.println(this.tabDoJogo);
    }

    public boolean querMaisUmaPartida ()
    {
        // perguntar se deseja jogar de novo
        // permitindo digitacao e retornando
        // true ou false, conforme a resposta
        while (true)
        {
            try
            {
                System.out.print("\nDeseja jogar mais uma partida? (S/N): ");
                String resp = teclado.readLine();
                if (resp == null)
                    return false;
                resp = resp.trim().toUpperCase();
                if (resp.equals("S"))
                    return true;
                if (resp.equals("N"))
                    return false;
                System.out.println("Opcao invalida! Digite S para Sim ou N para Nao.");
            }
            catch (IOException erro)
            {
                throw new UncheckedIOException("Nao foi possivel ler a resposta sobre outra partida.", erro);
            }
        }
    }
}
