
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
            System.out.println("getRType " + getStockType(i));
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
        return RList.get(ressource).getStock();
    }
    
    // Renvoie le type de la ressource n°rNumber du tableau du producteur
    public TYPE getStockType( int rNumber)
    {
        return RList.get(rNumber).getStockType();
    }

    // Renvoie une liste contenant tous les types du producteur
    public SerializableList<TYPE> getStockTypes()
    {
        int i;
        SerializableList<TYPE> L = new SerializableList<TYPE>();;
        for(i=0 ; i < RList.size() ; i++)
            L.add( RList.get(i).getStockType() );
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
                        RList.get(i).increaseRessource( quantity );
                        System.out.println("ressource " + RList.get(i).getStockType() + " : " + RList.get(i).getStock());
                    }
                    try { Thread.sleep(ms); }catch (InterruptedException re) { System.out.println(re) ; };
                }
            }
        };
        t.start();
    }
    
    public int getStock(int quantity, TYPE T)
    {
        // Commence par compter le nombre de ressources de ce type chez ce producteur 
        int nType = 0, total = 0, takenRessources = 0 , i;
        int RNonDivisibles;
        ArrayList<Ressource> RL = new ArrayList<Ressource>(); // y met les ressources de ce type

        for (i = 0 ; i < RList.size() ; i++)
        {
            if(RList.get(i).getStockType() == T)
            {
                nType++;
                RL.add(RList.get(i));
                total +=RList.get(i).getStock();
            }
        }

        // on prend des ressources en proportionnelle arrondi a la partie entière
        for( i=0 ; i < nType ; i++)
        {
            takenRessources += RL.get(i).takeRessource( (RL.get(i).getStock() / total) * quantity);
        }
        
        System.out.println("on a " + takenRessources + " et il faut : " + quantity);
        
        // Maintenant il faut chercher le restant des ressources 
        i=0;
        while(takenRessources != quantity)
        {
            takenRessources += RL.get(i).takeRessource( quantity - takenRessources );
        }
        
        // Maintenant qu'on a toutes les ressources (ou qu'on a prit ce qu'on pouvait)
        // On met à jour les ressources
        for(i=0 ; i < RList.size() ; i++)
        {
            if( RList.get(i).getStockType() == T)
            {
                RList.get(i).setRessource( RL.get(0).getStock() );
                RL.remove( 0 );
            }
        }
        
        return takenRessources;
    }
    
}

