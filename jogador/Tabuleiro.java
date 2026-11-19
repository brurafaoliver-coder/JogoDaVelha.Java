package jogador;

import java.util.Arrays;

public class Tabuleiro implements Cloneable
{
    // a principio, toda igual a null
    private Marca matriz [][] = new Marca [3][3];

    public Tabuleiro ()
    {
        // A matriz inicializada acima possui nove posicoes vazias.
    }

    private void validarPosicao (int lin, int col) throws Exception
    {
        if (lin < 0 || lin > 2 || col < 0 || col > 2)
            throw new Exception("Posicao invalida: indices devem estar entre 0 e 2.");
    }

    public boolean haMarcaNaPosicao (int lin, int col) throws Exception
    {
        // Retornar false para null e true para uma posicao preenchida.
        validarPosicao(lin, col);
        return matriz[lin][col] != null;
    }

    public Marca getMarcaNaPosicao (int lin, int col) throws Exception
    {
        validarPosicao(lin, col);
        return matriz[lin][col];
    }

    public void setMarcaNaPosicao(Marca mrc, int lin, int col) throws Exception
    {
        validarPosicao(lin, col);

        if (mrc == null)
            throw new Exception("A marca nao pode ser nula.");

        if (haMarcaNaPosicao(lin, col))
            throw new Exception("Posicao ocupada: escolha outra casa.");

        // Copia a marca para nao compartilhar o mesmo objeto entre tabuleiros.
        matriz[lin][col] = new Marca(mrc.getSimbolo());
    }

    @Override
    public String toString()
    {
        StringBuilder desenho = new StringBuilder();
        desenho.append("    1   2   3\n");

        for (int lin = 0; lin < 3; lin++)
        {
            desenho.append(lin + 1).append("   ");

            for (int col = 0; col < 3; col++)
            {
                if (matriz[lin][col] == null)
                    desenho.append(" ");
                else
                    desenho.append(matriz[lin][col].getSimbolo());

                if (col < 2)
                    desenho.append(" | ");
            }

            desenho.append("\n");

            if (lin < 2)
                desenho.append("   ---+---+---\n");
        }

        return desenho.toString();
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;

        if (!(obj instanceof Tabuleiro))
            return false;

        Tabuleiro outro = (Tabuleiro) obj;
        return Arrays.deepEquals(this.matriz, outro.matriz);
    }

    @Override
    public int hashCode()
    {
        return Arrays.deepHashCode(matriz);
    }

    // Construtor de copia: cria uma nova matriz e novas marcas.
    public Tabuleiro(Tabuleiro modelo) throws Exception
    {
        if (modelo == null)
            throw new Exception("O tabuleiro modelo nao pode ser nulo.");

        for (int lin = 0; lin < 3; lin++)
        {
            for (int col = 0; col < 3; col++)
            {
                if (modelo.matriz[lin][col] != null)
                {
                    this.matriz[lin][col] =
                        new Marca(modelo.matriz[lin][col].getSimbolo());
                }
            }
        }
    }

    @Override
    public Tabuleiro clone()
    {
        try
        {
            return new Tabuleiro(this);
        }
        catch (Exception e)
        {
            // Clonar um tabuleiro valido nao deveria falhar.
            throw new IllegalStateException("Nao foi possivel clonar o tabuleiro.", e);
        }
    }
}
