import java.io.Serializable;
import java.util.ArrayList;

// Juste une Arraylist qui étend Serializable pour pouvoir l'envoyer dans un retour de méthode RMI
public class SerializableList <T> implements Serializable
{ 
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    ArrayList<T> L;
    SerializableList()
    {
        L = new ArrayList<T>();
    }
    
    SerializableList( int i)
    {
        L = new ArrayList<T>(i);
    }
    
    public void add (T ajout)
    {
        L.add(ajout);
    }
    
    public void add (T ajout, int index)
    {
        L.add(index,ajout);
    }
    public T get (int index)
    {
        return L.get(index);
    }
    
    public int size()
    {
        return L.size();
    }
    
    public void remove(int i)
    {
        L.remove(i);
    }
    
    public boolean contains( T val)
    {
        return L.contains(val);
    }
} 

