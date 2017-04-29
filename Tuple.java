import java.io.Serializable;

public class Tuple<X,Y> implements Serializable
{
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    X x ;
    Y y;
    Tuple(X MachineName, Y port)
    {
        this.x = MachineName;
        this.y = port;
    }
}
