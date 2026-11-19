package jogador;

public class Tabuleiro implements Cloneable
{
    // a principio, toda igual a null
    private Marca matriz [][] = new Marca [3][3];

    public Tabuleiro ()
    {
        // a matriz ja e inicializada com Marca[3][3] contendo nulls
    }

    private void validarPosicao (int lin, int col) throws Exception
    {
        if (lin < 0 || lin > 2 || col < 0 || col > 2)
            throw new Exception("Posicao invalida: (" + lin + ", " + col + ")");
    }

    public boolean haMarcaNaPosicao (int lin, int col) throws Exception
    {
        // verifico se em matriz[lin][col]
        // tem null, retornando false em caso
        // afirmativo, ou true em caso negativo
        // dar excecao caso lin ou col invalido
        validarPosicao(lin, col);
        return this.matriz[lin][col] != null;
    }

    public Marca getMarcaNaPosicao (int lin, int col) throws Exception
    {
        // retornar a marca que tem em
        // matriz[lin][col]
        // dar excecao caso lin ou col invalido
        validarPosicao(lin, col);

        if (this.matriz[lin][col] == null)
            return null;

        return (Marca) this.matriz[lin][col].clone();
    }

    public void setMarcaNaPosicao (Marca mrc, int lin, int col) throws Exception
    {
        // colocar mrc em matriz[lin][col],
        // dar excecao caso lin ou col invalido
        // dar excecao caso mrc seja null
        validarPosicao(lin, col);

        if (mrc == null)
            throw new Exception("Marca ausente");

        if (haMarcaNaPosicao(lin, col))
            throw new Exception("Posicao ja ocupada: (" + lin + ", " + col + ")");

        this.matriz[lin][col] = (Marca) mrc.clone();
    }

    @Override
    public String toString ()
    {
        // retornar em um String o tabuleiro
        // formatado para ser exibido na tela
        StringBuilder sb = new StringBuilder();
        sb.append("    0   1   2\n");
        sb.append("  +---+---+---+\n");
        for (int l = 0; l < 3; l++)
        {
            sb.append(l).append(" |");
            for (int c = 0; c < 3; c++)
            {
                char s = (this.matriz[l][c] == null) ? ' ' : this.matriz[l][c].getSimbolo();
                sb.append(" ").append(s).append(" |");
            }
            sb.append("\n");
            sb.append("  +---+---+---+\n");
        }
        return sb.toString();
    }

    @Override
    public boolean equals (Object obj)
    {
        // retornar true se this for igual a obj ou false caso contrario
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        Tabuleiro outro = (Tabuleiro) obj;
        for (int l = 0; l < 3; l++)
        {
            for (int c = 0; c < 3; c++)
            {
                if (this.matriz[l][c] == null && outro.matriz[l][c] != null)
                    return false;
                if (this.matriz[l][c] != null && !this.matriz[l][c].equals(outro.matriz[l][c]))
                    return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode ()
    {
        // faça
        int ret = 1;
        for (int l = 0; l < 3; l++)
        {
            for (int c = 0; c < 3; c++)
            {
                ret = 31 * ret + (this.matriz[l][c] == null ? 0 : this.matriz[l][c].hashCode());
            }
        }
        return ret;
    }

    // construtor de copia
    public Tabuleiro (Tabuleiro modelo) throws Exception
    {
        // faça
        if (modelo == null)
            throw new Exception("Modelo ausente");

        for (int l = 0; l < 3; l++)
        {
            for (int c = 0; c < 3; c++)
            {
                if (modelo.matriz[l][c] != null)
                    this.matriz[l][c] = (Marca) modelo.matriz[l][c].clone();
                else
                    this.matriz[l][c] = null;
            }
        }
    }

    @Override
    public Object clone ()
    {
        // faça
        Tabuleiro ret = null;
        try
        {
            ret = new Tabuleiro(this);
        }
        catch (Exception erro)
        {}
        return ret;
    }
}
