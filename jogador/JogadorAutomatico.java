package jogador;

public class JogadorAutomatico implements Jogador
{
    private Marca     marca     = null;
    private Tabuleiro tabDoJogo = null;

    public JogadorAutomatico (Tabuleiro tab) throws Exception
    {
        if (tab == null)
            throw new Exception("Tabuleiro ausente");

        // atribuir tab para tabDoJogo
        tabDoJogo = tab;
        // fazer o jogador escolher sua marca
        // via sorteio de numeros aleatorio
        if ((int)(Math.random()*2) == 0)
            marca=new Marca('X');
        else
            marca=new Marca('O');
    }

    public JogadorAutomatico (Tabuleiro tab, Marca mrc) throws Exception
    {
        if (tab == null)
            throw new Exception("Tabuleiro ausente");
        if (mrc == null)
            throw new Exception("Marca ausente");

        // atribuir tab para tabDoJogo
        // e mrc para marca
        tabDoJogo = tab;
        marca = mrc;
    }

    @Override
    public Marca getMarca ()
    {
        // retorna a marca do jogador
        return marca;
    }

    @Override
    public void facaSuaJogada ()
    {
        try
        {
            Marca adversaria = new Marca(marca.getSimbolo() == 'X' ? 'O' : 'X');

            // tentando ganhar na linha 0
            if (tenteJogarNaSequencia(marca, 2, 0, 0, 0, 1, 0, 2))
                return;
            // tentando ganhar na linha 1
            else if (tenteJogarNaSequencia(marca, 2, 1, 0, 1, 1, 1, 2))
                return;
            // tentando ganhar na linha 2
            else if (tenteJogarNaSequencia(marca, 2, 2, 0, 2, 1, 2, 2))
                return;
            // tentando ganhar na coluna 0
            else if (tenteJogarNaSequencia(marca, 2, 0, 0, 1, 0, 2, 0))
                return;
            // tentando ganhar na coluna 1
            else if (tenteJogarNaSequencia(marca, 2, 0, 1, 1, 1, 2, 1))
                return;
            // tentando ganhar na coluna 2
            else if (tenteJogarNaSequencia(marca, 2, 0, 2, 1, 2, 2, 2))
                return;
            // tentando ganhar na diagonal principal
            else if (tenteJogarNaSequencia(marca, 2, 0, 0, 1, 1, 2, 2))
                return;
            // tentando ganhar na diagonal secundaria
            else if (tenteJogarNaSequencia(marca, 2, 0, 2, 1, 1, 2, 0))
                return;
            // tentando atrapalhar vitoria na linha 0
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 0, 0, 1, 0, 2))
                return;
            // tentando atrapalhar vitoria na linha 1
            else if (tenteJogarNaSequencia(adversaria, 2, 1, 0, 1, 1, 1, 2))
                return;
            // tentando atrapalhar vitoria na linha 2
            else if (tenteJogarNaSequencia(adversaria, 2, 2, 0, 2, 1, 2, 2))
                return;
            // tentando atrapalhar vitoria na coluna 0
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 0, 1, 0, 2, 0))
                return;
            // tentando atrapalhar vitoria na coluna 1
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 1, 1, 1, 2, 1))
                return;
            // tentando atrapalhar vitoria na coluna 2
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 2, 1, 2, 2, 2))
                return;
            // tentando atrapalhar vitoria na diagonal principal
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 0, 1, 1, 2, 2))
                return;
            // tentando atrapalhar vitoria na diagonal secundaria
            else if (tenteJogarNaSequencia(adversaria, 2, 0, 2, 1, 1, 2, 0))
                return;

            // Preparar: exatamente uma marca propria e duas casas vazias.
            // Isso exclui sequencias bloqueadas pelo adversario.
            // Escolher a primeira casa vazia na ordem das coordenadas:
            // esquerda para direita, cima para baixo; na secundaria,
            // do canto superior direito ao canto inferior esquerdo.
            // tentando jogada de futuro na linha 0
            else if (tenteJogarNaSequencia(marca, 1, 0, 0, 0, 1, 0, 2))
                return;
            // tentando jogada de futuro na linha 1
            else if (tenteJogarNaSequencia(marca, 1, 1, 0, 1, 1, 1, 2))
                return;
            // tentando jogada de futuro na linha 2
            else if (tenteJogarNaSequencia(marca, 1, 2, 0, 2, 1, 2, 2))
                return;
            // tentando jogada de futuro na coluna 0
            else if (tenteJogarNaSequencia(marca, 1, 0, 0, 1, 0, 2, 0))
                return;
            // tentando jogada de futuro na coluna 1
            else if (tenteJogarNaSequencia(marca, 1, 0, 1, 1, 1, 2, 1))
                return;
            // tentando jogada de futuro na coluna 2
            else if (tenteJogarNaSequencia(marca, 1, 0, 2, 1, 2, 2, 2))
                return;
            // tentando jogada de futuro na diagonal principal
            else if (tenteJogarNaSequencia(marca, 1, 0, 0, 1, 1, 2, 2))
                return;
            // tentando jogada de futuro na diagonal secundaria
            else if (tenteJogarNaSequencia(marca, 1, 0, 2, 1, 1, 2, 0))
                return;

            // termine jogando num lugar qualquer que esteja livre
            else
            {
                for (int lin = 0; lin < 3; lin++)
                    for (int col = 0; col < 3; col++)
                        if (!tabDoJogo.haMarcaNaPosicao(lin, col))
                        {
                            tabDoJogo.setMarcaNaPosicao(marca, lin, col);
                            return;
                        }

                // Tabuleiro cheio: nao existe jogada valida; nao alterar nada.
            }
        }
        catch (Exception erro)
        {
            // A interface nao permite lancar uma excecao verificada.
            // Preservar a causa para que falhas inesperadas sejam diagnosticadas.
            throw new IllegalStateException("Nao foi possivel realizar a jogada automatica.", erro);
        }
    }

    // Conta marcas e casas vazias nas tres posicoes recebidas.
    // quantidade vale 2 para ganhar/bloquear e 1 para preparar.
    // Retorna true somente depois de colocar uma unica marca do computador.
    private boolean tenteJogarNaSequencia (Marca procurada, int quantidade,
                                          int lin0, int col0,
                                          int lin1, int col1,
                                          int lin2, int col2) throws Exception
    {
        int linhas [] = {lin0, lin1, lin2};
        int colunas [] = {col0, col1, col2};
        int marcas = 0;
        int vazias = 0;
        int primeiraVazia = -1;

        for (int pos = 0; pos < 3; pos++)
        {
            if (!tabDoJogo.haMarcaNaPosicao(linhas[pos], colunas[pos]))
            {
                vazias++;
                if (primeiraVazia == -1)
                    primeiraVazia = pos;
            }
            else if (procurada.equals(tabDoJogo.getMarcaNaPosicao(linhas[pos], colunas[pos])))
                marcas++;
        }

        // A soma deve preencher as tres casas, sem nenhuma marca contraria.
        if (marcas == quantidade && vazias == 3 - quantidade)
        {
            tabDoJogo.setMarcaNaPosicao(marca, linhas[primeiraVazia], colunas[primeiraVazia]);
            return true;
        }

        return false;
    }
}
