package jogador;

import java.io.*;

public class JogadorHumano implements Jogador
{
    private Marca          marca     = null;
    private Tabuleiro      tabDoJogo = null;
    private BufferedReader teclado   = new BufferedReader (new InputStreamReader (System.in));

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
            try
            {
                System.out.print("Escolha sua marca (X ou O) - JogadorHumano.java:27");
                String digitado = this.teclado.readLine();
                if (digitado == null)
                {
                    smb = 'X';
                    break;
                }
                digitado = digitado.trim().toUpperCase();
                if (digitado.length() > 0)
                    smb = digitado.charAt(0);
                if (smb != 'X' && smb != 'O')
                    System.out.println("Opcao invalida! Digite apenas 'X' ou 'O'. - JogadorHumano.java:38");
            }
            catch (IOException erro)
            {
                System.out.println("Erro na leitura. Tente novamente. - JogadorHumano.java:42");
            }
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

    public Marca getMarca ()
    {
        // retorna a marca do jogador
        return (Marca) this.marca.clone();
    }

    public void facaSuaJogada ()
    {
        // pergunta para o jogador onde ele quer
        // jogar, permitindo digitacao e chamando
        // os métodos apropriados de tabDoJogo
        System.out.println("\n - JogadorHumano.java:73");
        System.out.println("Sua vez de jogar! Sua marca e: - JogadorHumano.java:74" + this.marca);
        System.out.println("Tabuleiro atual: - JogadorHumano.java:75");
        System.out.println(this.tabDoJogo);

        while (true)
        {
            try
            {
                System.out.print("Digite a linha onde deseja jogar (0, 1 ou 2): - JogadorHumano.java:82");
                String linStr = this.teclado.readLine();
                System.out.print("Digite a coluna onde deseja jogar (0, 1 ou 2): - JogadorHumano.java:84");
                String colStr = this.teclado.readLine();

                if (linStr == null || colStr == null)
                {
                    System.out.println("Entrada finalizada. - JogadorHumano.java:89");
                    return;
                }

                int lin = Integer.parseInt(linStr.trim());
                int col = Integer.parseInt(colStr.trim());

                if (lin < 0 || lin > 2 || col < 0 || col > 2)
                {
                    System.out.println("Posicao invalida! Linha e coluna devem ser 0, 1 ou 2. - JogadorHumano.java:98");
                    continue;
                }

                if (this.tabDoJogo.haMarcaNaPosicao(lin, col))
                {
                    System.out.println("Essa posicao ja esta ocupada! Escolha outra. - JogadorHumano.java:104");
                    continue;
                }

                this.tabDoJogo.setMarcaNaPosicao(this.marca, lin, col);
                System.out.println("\nJogada realizada com sucesso: - JogadorHumano.java:109");
                System.out.println(this.tabDoJogo);
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Entrada invalida! Digite apenas numeros inteiros (0, 1 ou 2). - JogadorHumano.java:115");
            }
            catch (Exception erro)
            {
                System.out.println("Erro: - JogadorHumano.java:119" + erro.getMessage() + ". Tente novamente.");
            }
        }
    }

    public void vcGanhou ()
    {
        // mostra mensagem indicando vitoria
        System.out.println("\n================================== - JogadorHumano.java:127");
        System.out.println("PARABENS! VOCE VENCEU! :) - JogadorHumano.java:128");
        System.out.println("================================== - JogadorHumano.java:129");
        System.out.println(this.tabDoJogo);
    }

    public void vcPerdeu ()
    {
        // mostra mensagem indicando derrota
        System.out.println("\n================================== - JogadorHumano.java:136");
        System.out.println("QUE PENA! VOCE PERDEU! :( - JogadorHumano.java:137");
        System.out.println("================================== - JogadorHumano.java:138");
        System.out.println(this.tabDoJogo);
    }

    public void deuVelha ()
    {
        // mostra mensagem indicando empate
        System.out.println("\n================================== - JogadorHumano.java:145");
        System.out.println("DEU VELHA! PARTIDA EMPATADA! - JogadorHumano.java:146");
        System.out.println("================================== - JogadorHumano.java:147");
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
                System.out.print("\nDeseja jogar mais uma partida? (S/N): - JogadorHumano.java:160");
                String resp = this.teclado.readLine();
                if (resp == null)
                    return false;
                resp = resp.trim().toUpperCase();
                if (resp.startsWith("S"))
                    return true;
                if (resp.startsWith("N"))
                    return false;
                System.out.println("Opcao invalida! Digite 'S' para Sim ou 'N' para Nao. - JogadorHumano.java:169");
            }
            catch (IOException erro)
            {
                System.out.println("Erro na leitura. Tente novamente. - JogadorHumano.java:173");
            }
        }
    }
}
