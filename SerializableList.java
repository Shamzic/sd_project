import java.io.Serializable;
import java.util.ArrayList;

// Juste une Arraylist qui étend Serializable pour pouvoir l'envoyer dans un retour de méthode RMI
public class SerializableList <T> implements Serializable
{ 
    ArrayList<T> L;
    SerializableList()
    {
        L = new ArrayList<T>();
    }
    
    public void add (T ajout)
    {
        L.add(ajout);
    }
    
    public T get (int index)
    {
        return L.get(index);
    }
    
    public int size()
    {
        return L.size();
    }
    
} 

