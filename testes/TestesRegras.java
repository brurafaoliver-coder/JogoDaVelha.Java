import jogador.*;

// Testes sem bibliotecas externas. Todas as regras usam o Tabuleiro real.
public class TestesRegras
{
    private static int verificacoes;
    private static final int[][] SEQUENCIAS = {
        {0,1,2}, {3,4,5}, {6,7,8},
        {0,3,6}, {1,4,7}, {2,5,8}, {0,4,8}, {2,4,6}
    };

    private static void conferir(boolean condicao, String mensagem)
    {
        verificacoes++;
        if (!condicao)
            throw new AssertionError(mensagem);
    }

    private static Tabuleiro criar(String casas) throws Exception
    {
        Tabuleiro tab = new Tabuleiro();
        for (int casa = 0; casa < 9; casa++)
            if (casas.charAt(casa) != '.')
                tab.setMarcaNaPosicao(new Marca(casas.charAt(casa)), casa / 3, casa % 3);
        return tab;
    }

    private static char[] ler(Tabuleiro tab) throws Exception
    {
        char[] casas = new char[9];
        for (int casa = 0; casa < 9; casa++)
        {
            Marca marca = tab.getMarcaNaPosicao(casa / 3, casa % 3);
            casas[casa] = marca == null ? '.' : marca.getSimbolo();
        }
        return casas;
    }

    private static void testarMarca() throws Exception
    {
        Marca x = new Marca('X');
        Marca outroX = new Marca('X');
        Marca o = new Marca('O');
        conferir(x.equals(x) && x.equals(outroX) && outroX.equals(x), "Marcas iguais");
        conferir(!x.equals(o) && !x.equals(null) && !x.equals("X"), "Marcas diferentes");
        conferir(x.hashCode() == outroX.hashCode(), "Hash de marcas iguais");
        conferir(x.toString().equals("X") && o.getSimbolo() == 'O', "Simbolo e texto");
        conferir(x.equals(x.clone()) && x.clone() != x, "Copia da marca");
        for (int codigo = Character.MIN_VALUE; codigo <= Character.MAX_VALUE; codigo++)
        {
            char simbolo = (char)codigo;
            if (simbolo == 'X' || simbolo == 'O')
                conferir(new Marca(simbolo).getSimbolo() == simbolo, "Caractere valido");
            else
            {
                boolean rejeitado = false;
                try { new Marca(simbolo); }
                catch (Exception erro) { rejeitado = erro.getMessage() != null; }
                conferir(rejeitado, "Caractere invalido aceito: " + codigo);
            }
        }
    }

    private static void testarTabuleiro() throws Exception
    {
        Tabuleiro tab = new Tabuleiro();
        for (int lin = 0; lin < 3; lin++)
            for (int col = 0; col < 3; col++)
                conferir(!tab.haMarcaNaPosicao(lin, col) &&
                         tab.getMarcaNaPosicao(lin, col) == null, "Casa inicial vazia");

        int[][] invalidas = {{-1,0},{3,0},{0,-1},{0,3}};
        for (int[] pos : invalidas)
        {
            boolean ha = false, get = false, set = false;
            try { tab.haMarcaNaPosicao(pos[0], pos[1]); }
            catch (Exception erro) { ha = true; }
            try { tab.getMarcaNaPosicao(pos[0], pos[1]); }
            catch (Exception erro) { get = true; }
            try { tab.setMarcaNaPosicao(new Marca('X'), pos[0], pos[1]); }
            catch (Exception erro) { set = true; }
            conferir(ha && get && set, "Indices devem ser rejeitados pelos tres metodos");
        }
        boolean nula = false;
        try { tab.setMarcaNaPosicao(null, 0, 0); }
        catch (Exception erro) { nula = true; }
        conferir(nula && !tab.haMarcaNaPosicao(0, 0), "Marca nula");
        tab.setMarcaNaPosicao(new Marca('X'), 0, 0);
        boolean ocupada = false;
        try { tab.setMarcaNaPosicao(new Marca('O'), 0, 0); }
        catch (Exception erro) { ocupada = true; }
        conferir(ocupada && tab.getMarcaNaPosicao(0,0).equals(new Marca('X')), "Nao sobrescrever");

        Tabuleiro copia = new Tabuleiro(tab);
        Tabuleiro clone = tab.clone();
        conferir(tab.equals(copia) && copia.equals(tab) && tab.equals(clone), "Igualdade por conteudo");
        conferir(tab.hashCode() == copia.hashCode() && tab.hashCode() == clone.hashCode(), "Hash do tabuleiro");
        conferir(tab.getMarcaNaPosicao(0,0) != copia.getMarcaNaPosicao(0,0) &&
                 tab.getMarcaNaPosicao(0,0) != clone.getMarcaNaPosicao(0,0), "Marcas copiadas");
        copia.setMarcaNaPosicao(new Marca('O'), 1, 1);
        clone.setMarcaNaPosicao(new Marca('O'), 2, 2);
        conferir(!tab.haMarcaNaPosicao(1,1) && !tab.haMarcaNaPosicao(2,2), "Matrizes independentes");
        conferir(!tab.equals(copia) && !tab.equals(null) && !tab.equals("tab"), "Igualdade diferente");
        conferir(tab.toString().contains("1   2   3"), "Coordenadas visiveis");
        boolean modeloNulo = false;
        try { new Tabuleiro((Tabuleiro)null); }
        catch (Exception erro) { modeloNulo = true; }
        conferir(modeloNulo, "Copia de tabuleiro nulo");
    }

    private static char vencedor(char[] casas)
    {
        for (int[] s : SEQUENCIAS)
            if (casas[s[0]] != '.' && casas[s[0]] == casas[s[1]] && casas[s[0]] == casas[s[2]])
                return casas[s[0]];
        return '.';
    }

    // Simula o resultado de cada casa livre, sem chamar auxiliares do computador.
    private static int jogadaEsperada(char[] antes, char computador)
    {
        for (int prioridade = 1; prioridade <= 3; prioridade++)
        {
            char simulada = prioridade == 2 ? (computador == 'X' ? 'O' : 'X') : computador;
            int melhorSequencia = 8, melhorCasa = -1;
            for (int casa = 0; casa < 9; casa++)
            {
                if (antes[casa] != '.') continue;
                char[] depois = antes.clone();
                depois[casa] = simulada;
                for (int ordem = 0; ordem < 8; ordem++)
                {
                    int[] s = SEQUENCIAS[ordem];
                    if (s[0] != casa && s[1] != casa && s[2] != casa) continue;
                    boolean serve;
                    if (prioridade < 3)
                        serve = depois[s[0]] == simulada && depois[s[1]] == simulada &&
                                depois[s[2]] == simulada;
                    else
                    {
                        int livres = 0;
                        boolean semAdversario = true;
                        for (int pos : s)
                            if (depois[pos] == '.') livres++;
                            else if (depois[pos] != computador) semAdversario = false;
                        serve = livres == 1 && semAdversario;
                    }
                    if (serve && ordem < melhorSequencia)
                    {
                        melhorSequencia = ordem;
                        melhorCasa = casa;
                    }
                }
            }
            if (melhorCasa != -1) return melhorCasa;
        }
        for (int casa = 0; casa < 9; casa++)
            if (antes[casa] == '.') return casa;
        return -1;
    }

    private static void conferirJogada(Tabuleiro tab, Jogador jogador) throws Exception
    {
        char[] antes = ler(tab);
        Marca[] referencias = new Marca[9];
        for (int casa = 0; casa < 9; casa++)
            referencias[casa] = tab.getMarcaNaPosicao(casa / 3, casa % 3);
        int esperada = jogadaEsperada(antes, jogador.getMarca().getSimbolo());
        jogador.facaSuaJogada();
        char[] depois = ler(tab);
        int mudancas = 0;
        for (int casa = 0; casa < 9; casa++)
        {
            char previsto = casa == esperada ? jogador.getMarca().getSimbolo() : antes[casa];
            conferir(depois[casa] == previsto, "Prioridade ou casa divergente");
            if (antes[casa] != depois[casa]) mudancas++;
            if (referencias[casa] != null)
                conferir(referencias[casa] == tab.getMarcaNaPosicao(casa / 3, casa % 3),
                         "Marca ocupada deve permanecer no lugar");
        }
        conferir(mudancas == (esperada == -1 ? 0 : 1), "Uma jogada por chamada");
    }

    private static void testarConfiguracoes() throws Exception
    {
        char[] valores = {'.','X','O'};
        for (int codigo = 0; codigo < 19683; codigo++)
        {
            char[] casas = new char[9];
            int numero = codigo;
            boolean cheio = true;
            for (int casa = 0; casa < 9; casa++)
            {
                casas[casa] = valores[numero % 3];
                numero /= 3;
                if (casas[casa] == '.') cheio = false;
            }
            Tabuleiro tab = criar(new String(casas));
            AnalistaSituacional analista = new AnalistaSituacional(tab);
            Marca marcaVencedora = analista.getMarcaDoVencedor();
            conferir((marcaVencedora == null ? '.' : marcaVencedora.getSimbolo()) == vencedor(casas),
                     "Vencedor incorreto");
            conferir(analista.fimDeJogo() == (cheio || vencedor(casas) != '.'), "Fim incorreto");
            for (char simbolo : new char[] {'X','O'})
            {
                Tabuleiro copia = new Tabuleiro(tab);
                conferirJogada(copia, new JogadorAutomatico(copia, new Marca(simbolo)));
            }
        }
    }

    private static void testarCasosExplicitos() throws Exception
    {
        for (char simbolo : new char[] {'X','O'})
            for (int[] s : SEQUENCIAS)
            {
                Tabuleiro tab = new Tabuleiro();
                for (int casa : s)
                    tab.setMarcaNaPosicao(new Marca(simbolo), casa / 3, casa % 3);
                AnalistaSituacional analista = new AnalistaSituacional(tab);
                conferir(analista.getMarcaDoVencedor().equals(new Marca(simbolo)) &&
                         analista.fimDeJogo(), "Vitoria nas oito sequencias");
            }
        Tabuleiro empate = criar("XOXXOOOXX");
        conferir(new AnalistaSituacional(empate).getMarcaDoVencedor() == null &&
                 new AnalistaSituacional(empate).fimDeJogo(), "Empate");
        Tabuleiro ultima = criar("XOXOXOOX.");
        conferir(!new AnalistaSituacional(ultima).fimDeJogo(), "Antes da ultima casa");
        new JogadorAutomatico(ultima, new Marca('X')).facaSuaJogada();
        conferir(new AnalistaSituacional(ultima).getMarcaDoVencedor().equals(new Marca('X')), "Vitoria na ultima casa");

        for (char simbolo : new char[] {'X','O'})
        {
            Tabuleiro tab = new Tabuleiro();
            Jogador jogador = new JogadorAutomatico(tab, new Marca(simbolo));
            for (int chamada = 0; chamada < 12; chamada++) conferirJogada(tab, jogador);
        }
        Tabuleiro tab = new Tabuleiro();
        AnalistaSituacional original = new AnalistaSituacional(tab);
        AnalistaSituacional copia = new AnalistaSituacional(original);
        AnalistaSituacional clone = (AnalistaSituacional)original.clone();
        conferir(original.equals(copia) && original.equals(clone) &&
                 original.hashCode() == copia.hashCode(), "Igualdade dos analistas");
        conferir(!original.equals(null) && !original.equals("analista"), "Analistas diferentes");
        for (int col = 0; col < 3; col++) tab.setMarcaNaPosicao(new Marca('O'), 0, col);
        conferir(copia.getMarcaDoVencedor().equals(new Marca('O')) &&
                 clone.getMarcaDoVencedor().equals(new Marca('O')), "Copias observam o mesmo jogo");
        conferir(original.toString().contains(tab.toString()), "Texto do analista");

        boolean tabNulo = false, marcaNula = false, analistaNulo = false;
        try { new JogadorAutomatico(null); }
        catch (Exception erro) { tabNulo = true; }
        try { new JogadorAutomatico(new Tabuleiro(), null); }
        catch (Exception erro) { marcaNula = true; }
        try { new AnalistaSituacional((Tabuleiro)null); }
        catch (Exception erro) { analistaNulo = true; }
        conferir(tabNulo && marcaNula && analistaNulo, "Validacao de construtores");
        for (int i = 0; i < 200; i++)
        {
            Marca sorteada = new JogadorAutomatico(new Tabuleiro()).getMarca();
            conferir(sorteada.getSimbolo() == 'X' || sorteada.getSimbolo() == 'O', "Marca sorteada");
        }
    }

    private static void testarFalhas() throws Exception
    {
        final Exception causa = new Exception("Falha de consulta simulada");
        Tabuleiro defeituoso = new Tabuleiro()
        {
            @Override
            public boolean haMarcaNaPosicao(int lin, int col) throws Exception { throw causa; }
        };
        boolean computador = false, analista = false;
        try { new JogadorAutomatico(defeituoso, new Marca('X')).facaSuaJogada(); }
        catch (IllegalStateException erro) { computador = erro.getCause() == causa; }
        try { new AnalistaSituacional(defeituoso).fimDeJogo(); }
        catch (IllegalStateException erro) { analista = erro.getCause() == causa; }
        conferir(computador && analista, "Falhas inesperadas preservam a causa");

        Tabuleiro falhaAoVerificarCheio = new Tabuleiro()
        {
            private int consultas;
            @Override
            public boolean haMarcaNaPosicao(int lin, int col) throws Exception
            {
                // Oito sequencias vazias nao tem vencedor; a proxima
                // consulta e a primeira verificacao de tabuleiro cheio.
                if (++consultas == 9) throw causa;
                return false;
            }
        };
        boolean cheio = false;
        try { new AnalistaSituacional(falhaAoVerificarCheio).fimDeJogo(); }
        catch (IllegalStateException erro) { cheio = erro.getCause() == causa; }
        conferir(cheio, "Falha na verificacao de tabuleiro cheio deve ser diagnosticavel");

        Tabuleiro falhaNaEscrita = new Tabuleiro()
        {
            @Override
            public void setMarcaNaPosicao(Marca mrc, int lin, int col) throws Exception { throw causa; }
        };
        boolean escrita = false;
        try { new JogadorAutomatico(falhaNaEscrita, new Marca('X')).facaSuaJogada(); }
        catch (IllegalStateException erro) { escrita = erro.getCause() == causa; }
        conferir(escrita, "Falha de escrita preserva causa");
    }

    public static void main(String[] args) throws Exception
    {
        testarMarca();
        testarTabuleiro();
        testarCasosExplicitos();
        testarConfiguracoes();
        testarFalhas();
        System.out.println("REGRAS: " + verificacoes + " verificacoes aprovadas.");
        System.out.println("65.536 caracteres; 19.683 tabuleiros do analista; 39.366 cenarios do computador.");
        System.out.println("Inclui estados artificiais para conferir regras locais; todas as classes sao reais.");
    }
}
