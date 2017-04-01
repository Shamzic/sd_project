import java.io.Serializable;
import java.util.ArrayList;

public class SerializableList  implements Serializable
{ 
    ArrayList<Tuple> L;
    SerializableList()
    {
        L = new ArrayList<Tuple>();
    }
    
    public void add (String MachineName, int port)
    {
        L.add(new Tuple(MachineName,port));
    }
    
    public Tuple get (int index)
    {
        return L.get(index);
    }
    
    public int size()
    {
        return L.size();
    }
    
} 

