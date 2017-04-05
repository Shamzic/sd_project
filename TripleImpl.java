import java.io.Serializable;


// Juste une classe qui permet d'envoyer 3 int dans un return
public class TripleImpl  implements Serializable
{ 
    public final int x; 
    public final int y; 
    public final int z; 
    public TripleImpl(int x, int y, int z)
    { 
        this.x = x; 
        this.y = y; 
        this.z = z; 
    } 
} 

