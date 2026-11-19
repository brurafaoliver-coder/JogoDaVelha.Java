import jogador.*;
import java.io.*;
import java.nio.charset.StandardCharsets;

// Entrada simulada, mas os objetos e o Tabuleiro sao as classes reais.
public class TestesEntrada
{
    private static int verificacoes;

    private static void conferir(boolean condicao, String mensagem)
    {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    public static void main(String[] args) throws Exception
    {
        if (args.length == 1 && args[0].equals("falha"))
        {
            testarFalhaDeLeitura();
            return;
        }
        InputStream entradaOriginal = System.in;
        PrintStream saidaOriginal = System.out;
        ByteArrayOutputStream captura = new ByteArrayOutputStream();
        String entradas = "XO\n?\nx\nS\no\nabc\n1\n0\n1\n4\n1\n1\n1\n1\n2\ntalvez\nN\n";
        try
        {
            // Deve ocorrer antes da primeira instancia de JogadorHumano.
            System.setIn(new ByteArrayInputStream(entradas.getBytes(StandardCharsets.UTF_8)));
            System.setOut(new PrintStream(captura, true, StandardCharsets.UTF_8.name()));
            JogadorHumano primeiro = new JogadorHumano(new Tabuleiro());
            conferir(primeiro.getMarca().equals(new Marca('X')), "Rejeitar XO e aceitar x");
            conferir(primeiro.querMaisUmaPartida(), "Repeticao S");

            Tabuleiro tab = new Tabuleiro();
            tab.setMarcaNaPosicao(new Marca('X'), 0, 0);
            JogadorHumano segundo = new JogadorHumano(tab);
            conferir(segundo.getMarca().equals(new Marca('O')), "Leitor compartilhado preserva o da segunda partida");
            segundo.facaSuaJogada();
            int ocupadas = 0;
            for (int lin = 0; lin < 3; lin++)
                for (int col = 0; col < 3; col++)
                    if (tab.haMarcaNaPosicao(lin, col)) ocupadas++;
            conferir(ocupadas == 2 && tab.getMarcaNaPosicao(0,1).equals(new Marca('O')),
                     "Erros nao perdem turno; 1,2 da interface vira 0,1 internamente");
            conferir(tab.getMarcaNaPosicao(0,0).equals(new Marca('X')), "Casa ocupada preservada");
            conferir(!segundo.querMaisUmaPartida(), "Resposta invalida e N");

            boolean escolhaEOF = false, jogadaEOF = false;
            try { new JogadorHumano(new Tabuleiro()); }
            catch (EOFException erro) { escolhaEOF = true; }
            JogadorHumano semEntrada = new JogadorHumano(new Tabuleiro(), new Marca('X'));
            try { semEntrada.facaSuaJogada(); }
            catch (UncheckedIOException erro) { jogadaEOF = erro.getCause() instanceof EOFException; }
            conferir(escolhaEOF && jogadaEOF, "EOF nao escolhe marca nem conclui turno");
            conferir(!semEntrada.querMaisUmaPartida(), "EOF na repeticao encerra");
            boolean tabNulo = false, marcaNula = false;
            try { new JogadorHumano(null); }
            catch (Exception erro) { tabNulo = true; }
            try { new JogadorHumano(new Tabuleiro(), null); }
            catch (Exception erro) { marcaNula = true; }
            conferir(tabNulo && marcaNula, "Argumentos nulos");
        }
        finally
        {
            System.setIn(entradaOriginal);
            System.setOut(saidaOriginal);
        }
        String texto = captura.toString(StandardCharsets.UTF_8.name());
        conferir(texto.contains("Entrada invalida") && texto.contains("Posicao invalida") &&
                 texto.contains("ja esta ocupada") && texto.contains("Opcao invalida"), "Mensagens de validacao");
        conferir(!texto.contains("JogadorHumano.java:"), "Sem textos de depuracao");
        System.out.println("ENTRADA: " + verificacoes + " verificacoes aprovadas.");
    }

    private static void testarFalhaDeLeitura() throws Exception
    {
        final IOException causa = new IOException("Falha de leitura simulada");
        System.setIn(new InputStream()
        {
            @Override
            public int read() throws IOException { throw causa; }
        });
        boolean escolha = false, jogada = false, repeticao = false;
        try { new JogadorHumano(new Tabuleiro()); }
        catch (IOException erro) { escolha = erro == causa; }
        JogadorHumano humano = new JogadorHumano(new Tabuleiro(), new Marca('X'));
        try { humano.facaSuaJogada(); }
        catch (UncheckedIOException erro) { jogada = erro.getCause() == causa; }
        try { humano.querMaisUmaPartida(); }
        catch (UncheckedIOException erro) { repeticao = erro.getCause() == causa; }
        conferir(escolha && jogada && repeticao, "Falhas internas nao devem virar repeticao de entrada");
        System.out.println("\nLEITURA: falhas simuladas nas tres leituras preservam sua causa.");
    }
}
