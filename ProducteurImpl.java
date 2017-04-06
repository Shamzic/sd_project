
//package com.jmdoudoux.dej.thread;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
    
class ProducteurImpl extends UnicastRemoteObject implements Producteur
{
    public ArrayList<Ressource> RList;
    int id;
	ProducteurImpl (int id, int RI, int RD)
    throws RemoteException
	{
        int i;
        this.id = id;
        RList = new ArrayList<Ressource> (RD);
        for (i=0; i< RD; i++) // initialise toutes les ressources du producteur
            RList.add(i, new Ressource(RI));
        
        for (i=0; i< RD ; i++)
        {
            System.out.println("getRType " + getRessourceType(i));
        }
	}
	 // sert à rien 
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut on vient de t'ajouter");
    }
    
    // renvoie la quantité de la ressource N°ressource 
    public int askRessourceAmount( int ressource)
        throws RemoteException 
    {
        return RList.get(ressource).getRessource();
    }
    
    // Renvoie le type de la ressource n°rNumber du tableau du producteur
    public TYPE getRessourceType( int rNumber)
    {
        return RList.get(rNumber).getRessourceType();
    }

    // Renvoie une liste contenant tous les types du producteur
    public SerializableList<TYPE> getRessourceTypes()
    {
        int i;
        SerializableList<TYPE> L = new SerializableList<TYPE>();;
        for(i=0 ; i < RList.size() ; i++)
            L.add( RList.get(i).getRessourceType() );
        return L;
    }

    public void decreaseRessourceAmount(int ressource, int x)
        throws RemoteException 
    {
        RList.get(ressource).decreaseRessource(x);
    }
    
    public void fonctionThread ( int ms, int quantity)
    {
        Thread t = new Thread()
        {
            public void run()
            {
                int i;
                while(true)
                {
                    System.out.println("j'ajoute des ressources");
                    for(i = 0 ; i< RList.size() ; i++)
                    {
                        RList.get(i).addRessource( quantity );
                        System.out.println("ressource " + RList.get(i).getRessourceType() + " : " + RList.get(i).getRessource());
                    }
                    try { Thread.sleep(ms); }catch (InterruptedException re) { System.out.println(re) ; };
                }
            }
        };
        t.start()
    }
    
    public int getRessource(int quantity, TYPE T)
    {
        // Commence par compter le nombre de ressources de ce type chez ce producteur 
        int nType = 0, total = 0, takenRessources = 0 , i;
        int RNonDivisibles;
        ArrayList<Ressource> RL = new ArrayList<Ressource>(); // y met les ressources de ce type

        for (i = 0 ; i < RList.size() ; i++)
        {
            if(RList.get(i).getRessourceType() == T)
            {
                nType++;
                RL.add(RList.get(i));
                total +=RList.get(i).getRessourceAmount();
            }
        }

        // on prend des ressources en proportionnelle arrondi a la partie entière
        for( i=0 ; i < nType ; i++)
        {
            takenRessources += RL.get(i).takeRessource( (RL.getRessource(i) / total) * quantity);
        }
        
        System.out.println("on a " + takenRessources + " et il faut : " + quantity);
        
        return takenRessources;
        // nombre de ressource qu'on peut pas partager équitablement entre les producteurs
        //~ // exemple : 2 prod et besoin de 5 ressources : 5 % 2 = 1 ressource non divisible équitablement
        //~ RNonDivisibles = quantity % nType; 
        //~ 
        //~ // On regarde la parité nbRessourcesProduites et parité du besoin de ressources
        //~ // Si la parité est bonne -> rien à faire en plus
        //~ // Si elle est pas bonne -> on fait un modulo du nombre de producteurs et on enlève 
        //~ // ça AUX PRODUCTEURS QUI ONT LE PLUS (commence par le premier, si pas assez va au deuxième, etc...)
        //~ 
        //~ 
        //~ if( total > quantity) // il y a assez de ressources
        //~ {
            //~ 
        //~ }
        //~ else // il y en a pas assez -> prend toutes les ressources de chaque
        //~ {
            //~ 
        //~ }
        //~ 
    }
    
}

