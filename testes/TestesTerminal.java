import jogador.*;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.*;

// Executa Velha em processos reais, com entrada/saida de terminal por pipes.
// A enumeracao abaixo apenas encontra roteiros de teste; nao altera a estrategia.
public class TestesTerminal
{
    private static int verificacoes;
    private static int sessoes;
    private static Path pastaRelatorios;
    private static final String MARCA = "Escolha sua marca (X ou O): ";
    private static final String LINHA = "Digite a linha onde deseja jogar (1, 2 ou 3): ";
    private static final String COLUNA = "Digite a coluna onde deseja jogar (1, 2 ou 3): ";
    private static final String REPETIR = "Deseja jogar mais uma partida? (S/N): ";
    private static final Map<String, Map<String,List<Integer>>> ROTEIROS = new HashMap<>();

    private static void conferir(boolean condicao, String mensagem)
    {
        verificacoes++;
        if (!condicao) throw new AssertionError(mensagem);
    }

    private static int contar(String texto, String trecho)
    {
        int quantidade = 0, posicao = 0;
        while ((posicao = texto.indexOf(trecho, posicao)) != -1)
        {
            quantidade++;
            posicao += trecho.length();
        }
        return quantidade;
    }

    private static void buscarRoteiros(Tabuleiro tab, boolean vezHumano, char humano,
                                       List<Integer> jogadas, Map<String,List<Integer>> encontrados) throws Exception
    {
        AnalistaSituacional analista = new AnalistaSituacional(tab);
        if (analista.fimDeJogo())
        {
            Marca vencedor = analista.getMarcaDoVencedor();
            String resultado = vencedor == null ? "EMPATE" :
                (vencedor.getSimbolo() == humano ? "VITORIA" : "DERROTA");
            if (!encontrados.containsKey(resultado))
                encontrados.put(resultado, new ArrayList<>(jogadas));
            return;
        }
        if (encontrados.size() == 3) return;
        if (vezHumano)
        {
            for (int casa = 0; casa < 9; casa++)
                if (!tab.haMarcaNaPosicao(casa / 3, casa % 3))
                {
                    Tabuleiro proximo = new Tabuleiro(tab);
                    proximo.setMarcaNaPosicao(new Marca(humano), casa / 3, casa % 3);
                    jogadas.add(casa);
                    buscarRoteiros(proximo, false, humano, jogadas, encontrados);
                    jogadas.remove(jogadas.size() - 1);
                }
        }
        else
        {
            char computador = humano == 'X' ? 'O' : 'X';
            new JogadorAutomatico(tab, new Marca(computador)).facaSuaJogada();
            buscarRoteiros(tab, true, humano, jogadas, encontrados);
        }
    }

    private static class Sessao implements AutoCloseable
    {
        final Process processo;
        final BufferedReader saida;
        final Writer entrada;
        final StringBuilder texto = new StringBuilder();
        final int numero = ++sessoes;
        int cursor;
        boolean humanoComeca;
        char humano;
        int inicioPartida;

        Sessao() throws IOException
        {
            String executavel = Paths.get(System.getProperty("java.home"), "bin",
                System.getProperty("os.name").toLowerCase().contains("win") ? "java.exe" : "java").toString();
            processo = new ProcessBuilder(executavel, "-Dfile.encoding=UTF-8", "-cp",
                System.getProperty("java.class.path"), "Velha").redirectErrorStream(true).start();
            saida = new BufferedReader(new InputStreamReader(processo.getInputStream(), StandardCharsets.UTF_8));
            entrada = new OutputStreamWriter(processo.getOutputStream(), StandardCharsets.UTF_8);
        }

        void lerDisponivel() throws IOException
        {
            while (saida.ready())
            {
                int caractere = saida.read();
                if (caractere == -1) break;
                if (caractere != '\r') texto.append((char)caractere);
            }
        }

        int esperar(String... opcoes) throws Exception
        {
            long limite = System.nanoTime() + TimeUnit.SECONDS.toNanos(15);
            while (System.nanoTime() < limite)
            {
                lerDisponivel();
                int primeira = Integer.MAX_VALUE, opcao = -1;
                for (int i = 0; i < opcoes.length; i++)
                {
                    int posicao = texto.indexOf(opcoes[i], cursor);
                    if (posicao != -1 && posicao < primeira)
                    {
                        primeira = posicao;
                        opcao = i;
                    }
                }
                if (opcao != -1)
                {
                    cursor = primeira + opcoes[opcao].length();
                    return opcao;
                }
                if (!processo.isAlive())
                    throw new AssertionError("Processo encerrou antes do esperado:\n" + texto);
                Thread.sleep(5);
            }
            throw new AssertionError("Tempo excedido na sessao " + numero + ":\n" + texto);
        }

        void enviar(String valor) throws IOException
        {
            entrada.write(valor);
            entrada.flush();
        }

        void iniciarPartida(boolean marcaJaEnviada, boolean invalidarMarca) throws Exception
        {
            inicioPartida = cursor;
            if (esperar(MARCA, "Voce joga com ") == 0)
            {
                if (!marcaJaEnviada)
                    enviar(invalidarMarca ? "XO\n?\nx\n" : "X\n");
            }
            else
                conferir(!marcaJaEnviada, "Marca antecipada deve ser solicitada ao humano");
            esperar("Tabuleiro inicial:");
            String trecho = texto.substring(inicioPartida);
            Matcher marcas = Pattern.compile("Voce joga com ([XO])\\. Computador joga com ([XO])\\.").matcher(trecho);
            conferir(marcas.find(), "Exibir marcas");
            humano = marcas.group(1).charAt(0);
            conferir(humano != marcas.group(2).charAt(0), "Marcas devem ser diferentes");
            if (marcaJaEnviada) conferir(humano == 'O', "Preservar escolha O antecipada");
            humanoComeca = trecho.contains("Quem comeca: humano.");
            esperar(new Tabuleiro().toString());
            conferir(true, "Tabuleiro inicial vazio");
        }

        boolean jogar(String resultado, boolean invalidarJogadas) throws Exception
        {
            Map<String,List<Integer>> disponiveis = ROTEIROS.get(humanoComeca + ":" + humano);
            List<Integer> jogadas = disponiveis.get(resultado);
            if (jogadas == null) return false;
            StringBuilder roteiro = new StringBuilder();
            for (int i = 0; i < jogadas.size(); i++)
            {
                if (invalidarJogadas && i == 0)
                    roteiro.append("abc\n1\n0\n1\n4\n1\n");
                if (invalidarJogadas && i == 1)
                {
                    int ocupada = jogadas.get(0);
                    roteiro.append(ocupada / 3 + 1).append('\n').append(ocupada % 3 + 1).append('\n');
                }
                int casa = jogadas.get(i);
                roteiro.append(casa / 3 + 1).append('\n').append(casa % 3 + 1).append('\n');
            }
            enviar(roteiro.toString());
            esperar(REPETIR);
            String trecho = texto.substring(inicioPartida);
            String mensagem = resultado.equals("EMPATE") ? "DEU VELHA!" :
                (resultado.equals("VITORIA") ? "VOCE VENCEU!" : "VOCE PERDEU!");
            conferir(trecho.contains(mensagem), "Resultado terminal: " + resultado);
            conferir(contar(trecho, "VOCE VENCEU!") + contar(trecho, "VOCE PERDEU!") +
                     contar(trecho, "DEU VELHA!") == 1, "Um unico resultado por partida");
            conferir(contar(trecho, "Jogada realizada com sucesso:") == jogadas.size(),
                     "Quantidade de jogadas humanas");
            Matcher turnos = Pattern.compile("Sua vez de jogar! Sua marca e: ([XO])|Vez do computador \\(([XO])\\)\\.").matcher(trecho);
            boolean esperadoHumano = humanoComeca;
            int quantidadeTurnos = 0;
            while (turnos.find())
            {
                boolean turnoHumano = turnos.group(1) != null;
                conferir(turnoHumano == esperadoHumano, "Alternancia de todos os turnos");
                char marcaTurno = (turnoHumano ? turnos.group(1) : turnos.group(2)).charAt(0);
                conferir(marcaTurno == (turnoHumano ? humano : (humano == 'X' ? 'O' : 'X')),
                         "Marca de cada turno");
                conferir(turnos.start() < trecho.indexOf(mensagem), "Nenhum turno apos o resultado");
                esperadoHumano = !esperadoHumano;
                quantidadeTurnos++;
            }
            conferir(quantidadeTurnos >= 5 && quantidadeTurnos <= 9, "Quantidade valida de turnos");
            if (invalidarJogadas)
                conferir(trecho.contains("Entrada invalida") && trecho.contains("Posicao invalida") &&
                         trecho.contains("ja esta ocupada"), "Erros de entrada no programa real");
            return true;
        }

        void finalizar() throws Exception
        {
            conferir(processo.waitFor(15, TimeUnit.SECONDS), "Processo deve encerrar");
            lerDisponivel();
            conferir(processo.exitValue() == 0, "Saida normal do processo");
            conferir(!texto.toString().contains("Exception") &&
                     !texto.toString().contains("Nao foi possivel continuar"), "Sem falhas internas");
        }

        @Override
        public void close() throws IOException
        {
            if (processo.isAlive()) processo.destroyForcibly();
            lerDisponivel();
            Files.write(pastaRelatorios.resolve(String.format("terminal-%02d.txt", numero)),
                        texto.toString().getBytes(StandardCharsets.UTF_8));
            entrada.close();
            saida.close();
        }
    }

    private static void testarResultados() throws Exception
    {
        for (String resultado : new String[] {"VITORIA", "DERROTA", "EMPATE"})
        {
            boolean executado = false;
            for (int tentativa = 0; tentativa < 32 && !executado; tentativa++)
                try (Sessao sessao = new Sessao())
                {
                    sessao.iniciarPartida(false, true);
                    if (!sessao.jogar(resultado, true))
                    {
                        sessao.entrada.close();
                        sessao.finalizar();
                        continue;
                    }
                    sessao.enviar("N\n");
                    sessao.finalizar();
                    conferir(contar(sessao.texto.toString(), "=== Nova partida ===") == 1, "N encerra");
                    executado = true;
                }
            conferir(executado, "Executar resultado: " + resultado);
            System.out.println("TERMINAL: " + resultado + " e entradas invalidas aprovados.");
        }
    }

    private static void testarReinicio() throws Exception
    {
        try (Sessao sessao = new Sessao())
        {
            boolean marcaAntecipada = false;
            boolean inicioAnterior = false;
            for (int partida = 0; partida < 3; partida++)
            {
                sessao.iniciarPartida(marcaAntecipada, false);
                if (partida > 0)
                    conferir(sessao.humanoComeca != inicioAnterior, "Alternar quem comeca");
                conferir(sessao.jogar(partida == 2 ? "DERROTA" : "EMPATE", false), "Roteiro de reinicio");
                inicioAnterior = sessao.humanoComeca;
                if (partida < 2)
                {
                    marcaAntecipada = !sessao.humanoComeca;
                    sessao.enviar("talvez\nS\n" + (marcaAntecipada ? "O\n" : ""));
                }
            }
            sessao.enviar("N\n");
            sessao.finalizar();
            conferir(contar(sessao.texto.toString(), "=== Nova partida ===") == 3, "Tres partidas reais");
        }
        System.out.println("TERMINAL: tres partidas, reinicio vazio, alternancia, S/N e entrada antecipada aprovados.");
    }

    private static void testarEOF() throws Exception
    {
        boolean escolhaExecutada = false;
        for (int tentativa = 0; tentativa < 32 && !escolhaExecutada; tentativa++)
            try (Sessao sessao = new Sessao())
            {
                escolhaExecutada = sessao.esperar(MARCA, "Voce joga com ") == 0;
                sessao.entrada.close();
                sessao.finalizar();
                conferir(sessao.texto.toString().contains("Entrada encerrada."), "EOF encerra a sessao");
                conferir(!sessao.texto.toString().contains("VOCE PERDEU!"), "EOF nao e derrota");
            }
        conferir(escolhaExecutada, "EOF durante a escolha da marca executado");

        for (boolean duranteColuna : new boolean[] {false, true})
            try (Sessao sessao = new Sessao())
            {
                sessao.iniciarPartida(false, false);
                sessao.esperar(LINHA);
                if (duranteColuna)
                {
                    sessao.enviar("1\n");
                    sessao.esperar(COLUNA);
                }
                int turnosAntes = contar(sessao.texto.toString(), "Vez do computador");
                sessao.entrada.close();
                sessao.finalizar();
                String texto = sessao.texto.toString();
                conferir(texto.contains("Entrada encerrada."), "EOF durante jogada");
                conferir(!texto.contains("Jogada realizada com sucesso:") &&
                         !texto.contains(REPETIR), "Nao concluir jogada ou partida apos EOF");
                conferir(contar(texto, "Vez do computador") == turnosAntes, "Nao conceder turno apos EOF");
            }
        try (Sessao sessao = new Sessao())
        {
            sessao.iniciarPartida(false, false);
            conferir(sessao.jogar("EMPATE", false), "Partida antes de EOF na repeticao");
            sessao.entrada.close();
            sessao.finalizar();
            conferir(contar(sessao.texto.toString(), "=== Nova partida ===") == 1, "EOF na repeticao encerra");
        }
        System.out.println("TERMINAL: EOF na marca, linha, coluna e repeticao aprovados.");
    }

    private static void testarAlternanciaLonga() throws Exception
    {
        try (Sessao sessao = new Sessao())
        {
            boolean marcaAntecipada = false;
            boolean primeiroHumano = false;
            StringBuilder sequencia = new StringBuilder();
            for (int partida = 0; partida < 12; partida++)
            {
                sessao.iniciarPartida(marcaAntecipada, true);
                if (partida == 0) primeiroHumano = sessao.humanoComeca;
                boolean esperado = partida % 2 == 0 ? primeiroHumano : !primeiroHumano;
                conferir(sessao.humanoComeca == esperado, "Iniciante da partida " + (partida + 1));
                if (partida > 0) sequencia.append(", ");
                sequencia.append(partida + 1).append('=').append(sessao.humanoComeca ? 'H' : 'C');
                conferir(sessao.jogar("EMPATE", true), "Partida completa da serie longa");
                if (partida < 11)
                {
                    marcaAntecipada = !sessao.humanoComeca;
                    sessao.enviar("talvez\nS\n" + (marcaAntecipada ? "O\n" : ""));
                }
            }
            sessao.enviar("N\n");
            sessao.finalizar();
            conferir(contar(sessao.texto.toString(), "=== Nova partida ===") == 12, "Nenhuma partida omitida");
            conferir(contar(sessao.texto.toString(), "DEU VELHA!") == 12, "Doze resultados conferidos");
            String resumo = "Sessao " + sessao.numero + ": " + sequencia;
            Files.write(pastaRelatorios.resolve("alternancia.txt"),
                        resumo.getBytes(StandardCharsets.UTF_8));
            System.out.println("ALTERNANCIA: " + resumo);
        }
        System.out.println("ALTERNANCIA: 12 partidas na mesma JVM, com entradas invalidas, aprovadas.");
    }

    public static void main(String[] args) throws Exception
    {
        Path raizRelatorios = Paths.get("build", "relatorios");
        Files.createDirectories(raizRelatorios);
        pastaRelatorios = Files.createTempDirectory(raizRelatorios, "terminal-");
        for (boolean humanoComeca : new boolean[] {false, true})
            for (char humano : new char[] {'X','O'})
            {
                Map<String,List<Integer>> encontrados = new HashMap<>();
                buscarRoteiros(new Tabuleiro(), humanoComeca, humano, new ArrayList<>(), encontrados);
                ROTEIROS.put(humanoComeca + ":" + humano, encontrados);
                conferir(encontrados.containsKey("EMPATE") && encontrados.containsKey("DERROTA"),
                         "Roteiros para ambos os iniciantes");
            }
        testarResultados();
        testarReinicio();
        testarAlternanciaLonga();
        testarEOF();
        System.out.println("TERMINAL: " + verificacoes + " verificacoes em " + sessoes + " processos reais de Velha.");
        System.out.println("O sorteio original foi mantido; logs desta execucao em " + pastaRelatorios + ".");
    }
}
