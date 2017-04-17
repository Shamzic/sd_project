import java.io.Serializable;


// Juste une classe qui permet d'envoyer 4 int dans un return
public class QuadrupleImpl  implements Serializable
{ 
	public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    public final int x; 
    public final int y; 
    public final int z;
    public final int a;
    public QuadrupleImpl(int x, int y, int z, int a)
    { 
        this.x = x; 
        this.y = y; 
        this.z = z; 
        this.a = a;
    } 
} 

