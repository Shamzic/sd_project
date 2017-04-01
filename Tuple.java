import java.io.Serializable;

public class Tuple implements Serializable
    {
        String MN ;
        int port;
        Tuple(String MachineName, int port)
        {
            this.MN = MachineName;
            this.port = port;
        }
    }
