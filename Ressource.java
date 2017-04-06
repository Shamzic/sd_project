import java.util.Random;

// Ressource 
public class Ressource
{
    
    TYPE T = TYPE.ARGENT;
    
    int nombre;
    Ressource (int nombreInit)
    {
        nombre = nombreInit;
        Random rn = new Random();
        switch(rn.nextInt(3))
        {
            case 0:
            {
                T = TYPE.ARGENT;
                break;
            }
            case 1:
            {
                T = TYPE.OR;
                break;
            }
            case 2:
            {
                T = TYPE.BOIS;
            }
        }
        System.out.println("J'ai créé la ressource " + T);
    }
    
    Ressource (int nombreInit, TYPE T )
    {
        this.nombre = 0;
        this.T =T;
    }
    
    
    int getRessource()
    {
        return nombre;
    }
    
    TYPE getRessourceType()
    {
        return T;
    }
    
    void addRessource(int quantity)
    {
        nombre+=quantity;
    }
    
    int takeRessource(int quantity)
    {
        nombre -= quantity;
        return nombre;
    }
}
