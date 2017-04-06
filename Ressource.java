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

    // Constructeur utile pour init la liste de ressouces
    // d'un joueur
    Ressource (int nombreInit, TYPE T )
    {
        this.nombre = 0;
        this.T =T;
    }

    void setRessource(int quantite)
    {
        this.nombre=quantite;
    }

   	 // Retourne la quantité de la ressource
    int getRessource()
    {
        return nombre;
    }
    
    // Retourne le type de la ressource
    TYPE getRessourceType()
    {
        return T;
    }

    void increaseRessource(int x){
    	setRessource(getRessource()+x);
    }

    void decreaseRessource(int x){
    	int i = getRessource()-x;
    	if(i<0)
    		i=0;
    	setRessource(i);
    }
}
