package jogador;

import java.io.*;

public class JogadorHumano implements Jogador
{
    private Marca          marca     = null;
    private Tabuleiro      tabDoJogo = null;
    private BufferedReader teclado   = new BufferedReader
                                        (new InputStreamReader
                                        (System.in));

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
                System.out.print("Escolha sua marca (X ou O): - JogadorHumano.java:29");
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
                    System.out.println("Opcao invalida! Digite apenas 'X' ou 'O'. - JogadorHumano.java:40");
            }
            catch (IOException erro)
            {
                System.out.println("Erro na leitura. Tente novamente. - JogadorHumano.java:44");
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
        System.out.println("\n - JogadorHumano.java:77");
        System.out.println("Sua vez de jogar! Sua marca e: - JogadorHumano.java:78" + this.marca);
        System.out.println("Tabuleiro atual: - JogadorHumano.java:79");
        System.out.println(this.tabDoJogo);

        while (true)
        {
            try
            {
                System.out.print("Digite a linha onde deseja jogar (0, 1 ou 2): - JogadorHumano.java:86");
                String linStr = this.teclado.readLine();
                System.out.print("Digite a coluna onde deseja jogar (0, 1 ou 2): - JogadorHumano.java:88");
                String colStr = this.teclado.readLine();

                if (linStr == null || colStr == null)
                {
                    System.out.println("Entrada finalizada. - JogadorHumano.java:93");
                    return;
                }

                int lin = Integer.parseInt(linStr.trim());
                int col = Integer.parseInt(colStr.trim());

                if (lin < 0 || lin > 2 || col < 0 || col > 2)
                {
                    System.out.println("Posicao invalida! Linha e coluna devem ser 0, 1 ou 2. - JogadorHumano.java:102");
                    continue;
                }

                if (this.tabDoJogo.haMarcaNaPosicao(lin, col))
                {
                    System.out.println("Essa posicao ja esta ocupada! Escolha outra. - JogadorHumano.java:108");
                    continue;
                }

                this.tabDoJogo.setMarcaNaPosicao(this.marca, lin, col);
                System.out.println("\nJogada realizada com sucesso: - JogadorHumano.java:113");
                System.out.println(this.tabDoJogo);
                break;
            }
            catch (NumberFormatException e)
            {
                System.out.println("Entrada invalida! Digite apenas numeros inteiros (0, 1 ou 2). - JogadorHumano.java:119");
            }
            catch (Exception erro)
            {
                System.out.println("Erro: - JogadorHumano.java:123" + erro.getMessage() + ". Tente novamente.");
            }
        }
    }

    public void vcGanhou ()
    {
        // mostra mensagem indicando vitoria
        System.out.println("\n================================== - JogadorHumano.java:131");
        System.out.println("PARABENS! VOCE VENCEU! :) - JogadorHumano.java:132");
        System.out.println("================================== - JogadorHumano.java:133");
        System.out.println(this.tabDoJogo);
    }

    public void vcPerdeu ()
    {
        // mostra mensagem indicando derrota
        System.out.println("\n================================== - JogadorHumano.java:140");
        System.out.println("QUE PENA! VOCE PERDEU! :( - JogadorHumano.java:141");
        System.out.println("================================== - JogadorHumano.java:142");
        System.out.println(this.tabDoJogo);
    }

    public void deuVelha ()
    {
        // mostra mensagem indicando empate
        System.out.println("\n================================== - JogadorHumano.java:149");
        System.out.println("DEU VELHA! PARTIDA EMPATADA! - JogadorHumano.java:150");
        System.out.println("================================== - JogadorHumano.java:151");
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
                System.out.print("\nDeseja jogar mais uma partida? (S/N): - JogadorHumano.java:164");
                String resp = this.teclado.readLine();
                if (resp == null)
                    return false;
                resp = resp.trim().toUpperCase();
                if (resp.startsWith("S"))
                    return true;
                if (resp.startsWith("N"))
                    return false;
                System.out.println("Opcao invalida! Digite 'S' para Sim ou 'N' para Nao. - JogadorHumano.java:173");
            }
            catch (IOException erro)
            {
                System.out.println("Erro na leitura. Tente novamente. - JogadorHumano.java:177");
            }
        }
    }
}
