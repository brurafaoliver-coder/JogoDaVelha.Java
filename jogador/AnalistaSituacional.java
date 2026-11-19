package jogador;

public class AnalistaSituacional implements Cloneable
{
    private Tabuleiro tabDoJogo;

    public AnalistaSituacional (Tabuleiro tab) throws Exception
    {
        // colocar tab em tabDoJogo
        if (tab == null)
            throw new Exception("Tabuleiro ausente");
        this.tabDoJogo = tab;
    }

    public Marca getMarcaDoVencedor ()
    {
        // descobrir se ha um vencedor e retornar
        // a marca dele; retornara null, quando
        // nao houver vencedor
        try
        {
            // Linhas
            for (int l = 0; l < 3; l++)
            {
                if (this.tabDoJogo.haMarcaNaPosicao(l, 0) &&
                    this.tabDoJogo.haMarcaNaPosicao(l, 1) &&
                    this.tabDoJogo.haMarcaNaPosicao(l, 2))
                {
                    Marca m0 = this.tabDoJogo.getMarcaNaPosicao(l, 0);
                    Marca m1 = this.tabDoJogo.getMarcaNaPosicao(l, 1);
                    Marca m2 = this.tabDoJogo.getMarcaNaPosicao(l, 2);

                    if (m0.equals(m1) && m0.equals(m2))
                        return (Marca) m0.clone();
                }
            }

            // Colunas
            for (int c = 0; c < 3; c++)
            {
                if (this.tabDoJogo.haMarcaNaPosicao(0, c) &&
                    this.tabDoJogo.haMarcaNaPosicao(1, c) &&
                    this.tabDoJogo.haMarcaNaPosicao(2, c))
                {
                    Marca m0 = this.tabDoJogo.getMarcaNaPosicao(0, c);
                    Marca m1 = this.tabDoJogo.getMarcaNaPosicao(1, c);
                    Marca m2 = this.tabDoJogo.getMarcaNaPosicao(2, c);

                    if (m0.equals(m1) && m0.equals(m2))
                        return (Marca) m0.clone();
                }
            }

            // Diagonal principal
            if (this.tabDoJogo.haMarcaNaPosicao(0, 0) &&
                this.tabDoJogo.haMarcaNaPosicao(1, 1) &&
                this.tabDoJogo.haMarcaNaPosicao(2, 2))
            {
                Marca m0 = this.tabDoJogo.getMarcaNaPosicao(0, 0);
                Marca m1 = this.tabDoJogo.getMarcaNaPosicao(1, 1);
                Marca m2 = this.tabDoJogo.getMarcaNaPosicao(2, 2);

                if (m0.equals(m1) && m0.equals(m2))
                    return (Marca) m0.clone();
            }

            // Diagonal secundaria
            if (this.tabDoJogo.haMarcaNaPosicao(0, 2) &&
                this.tabDoJogo.haMarcaNaPosicao(1, 1) &&
                this.tabDoJogo.haMarcaNaPosicao(2, 0))
            {
                Marca m0 = this.tabDoJogo.getMarcaNaPosicao(0, 2);
                Marca m1 = this.tabDoJogo.getMarcaNaPosicao(1, 1);
                Marca m2 = this.tabDoJogo.getMarcaNaPosicao(2, 0);

                if (m0.equals(m1) && m0.equals(m2))
                    return (Marca) m0.clone();
            }
        }
        catch (Exception erro)
        {}

        return null;
    }

    private boolean tabuleiroCheio ()
    {
        // verificar se todas as posicoes do
        // tabuleiro estao preenchidas (!=null)
        // retornando true em caso afirmativo,
        // ou false em caso negativo
        try
        {
            for (int l = 0; l < 3; l++)
            {
                for (int c = 0; c < 3; c++)
                {
                    if (!this.tabDoJogo.haMarcaNaPosicao(l, c))
                        return false;
                }
            }
            return true;
        }
        catch (Exception erro)
        {
            return false;
        }
    }

    public boolean fimDeJogo ()
    {
        // se getMarcaDoVencedor != null,
        // retorna true
        // se tabuleiroCheio == true,
        // retorna true
        // caso contrario, retorna false
        if (this.getMarcaDoVencedor() != null)
            return true;

        if (this.tabuleiroCheio())
            return true;

        return false;
    }

    @Override
    public String toString ()
    {
        // faça
        return "Analista Situacional do Tabuleiro:\n" + this.tabDoJogo.toString();
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

        AnalistaSituacional outro = (AnalistaSituacional) obj;
        if (this.tabDoJogo == null && outro.tabDoJogo == null)
            return true;
        if (this.tabDoJogo == null || outro.tabDoJogo == null)
            return false;

        return this.tabDoJogo.equals(outro.tabDoJogo);
    }

    @Override
    public int hashCode ()
    {
        // faça
        int ret = 1;
        ret = 31 * ret + (this.tabDoJogo == null ? 0 : this.tabDoJogo.hashCode());
        return ret;
    }

    public AnalistaSituacional (AnalistaSituacional modelo) throws Exception
    {
        if (modelo == null)
            throw new Exception("Modelo ausente");
        this.tabDoJogo = modelo.tabDoJogo;
    }

    @Override
    public Object clone ()
    {
        AnalistaSituacional ret = null;
        try
        {
            ret = new AnalistaSituacional(this);
        }
        catch (Exception erro)
        {}
        return ret;
    }
}
