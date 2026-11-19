import jogador.*;
import java.io.*;

public class Velha
{
    public static void main (String args [])
    {
        Tabuleiro           tabDoJogo;

        Jogador             jogador  = null;
        JogadorHumano       jogadorH = null;
        JogadorAutomatico   jogadorA = null;

        AnalistaSituacional analista;

        boolean jogadorHumanoComeca = (int)(Math.random()*2) == 0;
        boolean continuar;

        do
        {
            try
            {
                System.out.println("\n=== Nova partida ===");
                tabDoJogo = new Tabuleiro ();

                if (jogadorHumanoComeca)
                {
                    jogadorH = new JogadorHumano (tabDoJogo);
                    jogadorA = new JogadorAutomatico
                              (tabDoJogo,
                               new Marca(jogadorH.getMarca().getSimbolo()=='X'?'O':'X'));
                }
                else
                {
                    jogadorA = new JogadorAutomatico (tabDoJogo);
                    jogadorH = new JogadorHumano
                              (tabDoJogo,
                               new Marca(jogadorA.getMarca().getSimbolo()=='X'?'O':'X'));
                }

                analista = new AnalistaSituacional (tabDoJogo);

                System.out.println("Voce joga com " + jogadorH.getMarca() +
                                   ". Computador joga com " + jogadorA.getMarca() + ".");
                System.out.println("Quem comeca: " + (jogadorHumanoComeca ? "humano." : "computador."));
                System.out.println("Tabuleiro inicial:");
                System.out.println(tabDoJogo);

                if (jogadorHumanoComeca)
                    jogador=jogadorH;
                else
                    jogador=jogadorA;

                do
                {
                    if (jogador == jogadorA)
                        System.out.println("\nVez do computador (" + jogadorA.getMarca() + ").");

                    jogador.facaSuaJogada();

                    if (jogador == jogadorA)
                        System.out.println(tabDoJogo);

                    // Verificar o fim antes de passar a vez ao proximo jogador.
                    if (analista.fimDeJogo())
                        break;

                    if (jogador==jogadorH)
                        jogador=jogadorA;
                    else
                        jogador=jogadorH;
                }
                while (!analista.fimDeJogo());

                Marca vencedor = analista.getMarcaDoVencedor();
                if (vencedor == null)
                    jogadorH.deuVelha ();
                else if (vencedor.equals(jogadorH.getMarca()))
                    jogadorH.vcGanhou ();
                else
                    jogadorH.vcPerdeu ();

                jogadorHumanoComeca = !jogadorHumanoComeca;
                continuar = jogadorH.querMaisUmaPartida();
            }
            catch (Exception e)
            {
                if (e instanceof EOFException ||
                    (e instanceof UncheckedIOException && e.getCause() instanceof EOFException))
                    System.out.println("\nEntrada encerrada. Ate logo!");
                else
                {
                    System.err.println("Nao foi possivel continuar a partida.");
                    e.printStackTrace(System.err);
                }
                return;
            }
        }
        while (continuar);

        System.out.println("Ate logo!");
    }
}
