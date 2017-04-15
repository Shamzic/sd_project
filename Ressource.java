import java.util.Random;
import java.io.Serializable;

// Ressource 
public class Ressource implements Serializable
{
    
    TYPE T = TYPE.ARGENT;
    
    int stock;
    Ressource (int stockInit)
    {
        stock = stockInit;
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

    Ressource (int stockInit, int type)
    {
        stock = stockInit;
        if(type == 0)
            T = TYPE.ARGENT;
        else if (type == 1)
            T = TYPE.OR;
        else
            T = TYPE.BOIS;
        System.out.println("J'ai créé la ressource " + T);

    }

    // Constructeur utile pour init la liste de ressouces
    // d'un joueur
    Ressource (int stockInit, TYPE T )
    {
        this.stock = stockInit ;
        this.T =T;
    }

    void setRessource(int quantite)
    {
        this.stock=quantite;
    }

   	 // Retourne la quantité de la ressource
    int getStock()
    {
        return stock;
    }
    
    // Retourne le type de la ressource
    TYPE getStockType()
    {
        return T;
    }


    void increaseRessource(int x){
    	setRessource(getStock()+x);
    }

    void decreaseRessource(int x){
    	int i = getStock()-x;
    	if(i<0)
    		i=0;
    	setRessource(i);
    }
    
    int takeRessource(int quantity)
    {
        int r = stock;
        if (r > quantity)
            r = quantity;
            
        stock -= r;
        
        return r;
        
    }

    public String  toString()
    {
        if(T == TYPE.ARGENT)
            return "Ressource d'argent de quantité "+String.valueOf(this.getStock());
        if(T == TYPE.OR)
            return "Ressource d'or de quantité "+String.valueOf(this.getStock());
        if(T == TYPE.BOIS)
            return "Ressource de bois de quantité "+String.valueOf(this.getStock());
        else 
             return "Ressource de type inconnu et de quantité "+String.valueOf(this.getStock());

    }
}
