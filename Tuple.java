import java.io.Serializable;

public class Tuple implements Serializable
{
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    String MN ;
    int port;
    Tuple(String MachineName, int port)
    {
        this.MN = MachineName;
        this.port = port;
    }
}
