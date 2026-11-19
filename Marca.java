package jogador;
public class Marca
{
    private char simbolo;

    public Marca (char smb) throws Exception
    {
        // validar smb, garantindo que ele
        // seja 'X' ou 'O' e coloca-lo em
        // simbolo
        // dar excecao em caso de valor invalido
        if (smb != 'X' && smb != 'O')
            throw new Exception("Simbolo invalido: deve ser 'X' ou 'O'");
        this.simbolo = smb;
    }

    public char getSimbolo ()
    {
        return this.simbolo;
    }

    @Override
    public String toString ()
    {
        return ""+simbolo;
    }

    @Override
    public boolean equals (Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (this.getClass() != obj.getClass())
            return false;

        Marca outra = (Marca) obj;
        return this.simbolo == outra.simbolo;
    }

    @Override
    public int hashCode ()
    {
        int ret = 1;
        ret = 31 * ret + Character.valueOf(this.simbolo).hashCode();
        return ret;
    }

    public Marca (Marca modelo) throws Exception
    {
        if (modelo == null)
            throw new Exception("Modelo ausente");
        this.simbolo = modelo.simbolo;
    }

    @Override
    public Object clone ()
    {
        Marca ret = null;
        try
        {
            ret = new Marca(this);
        }
        catch (Exception erro)
        {}
        return ret;
    }
}
