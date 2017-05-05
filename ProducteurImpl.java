
//package com.jmdoudoux.dej.thread;
import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

    
class ProducteurImpl extends UnicastRemoteObject implements Producteur
{
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    public ArrayList<Ressource> RList;
    int id;
    static MessageControle M; // Objet grâce auquel le producteur communique avec le contrôleur
    static Object ObjSynchro = new Object();
    Thread t;
    
    public static void main (String [] args)
    {
        ProducteurImpl P; // Objet qui instancie le producteur
        if ( args.length != 4)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <JoueurMachineName> <ProducteurPort>");
            System.exit(1);
        }
        try
		{
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            InitialInfoImpl  I = M.getProducteurInitialInfo(); // demande les informations initiales au contrôleur
            
            
            // initialise le serveur producteur
            P = new ProducteurImpl(I); // Fait le producteur
            Naming.rebind( "rmi://localhost:"+args[3] + "/Producteur", P);
            M.addProducteur( args[2], Integer.parseInt(args[3]) ); // Maintenant envoie ses "coordonnées" au Coordinateur
            System.out.println("Le producteur a été ajouté");
		}
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
    }
    
	ProducteurImpl (InitialInfoImpl  I)
    throws RemoteException
	{
        int i;
        this.id = I.IdProducteur;
        RList = new ArrayList<Ressource> (I.nbRessourcesDifferentes);
        for (i=0; i< I.nbRessourcesDifferentes; i++) // initialise toutes les ressources du producteur
        {
            if( id == 0 ) // s'assure qu'il existe au moins un producteur qui produit tout
                RList.add(i, new Ressource(I.nbRessourcesInitiales,i)); //-> fait 1 de chaque ressource
            else
                RList.add(i, new Ressource(I.nbRessourcesInitiales));
        }
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

    // Renvoie la liste des ressources + quantitée
    // Si le producteur possède 2 ressources identiques les mets dans un champ et l'envoie au joueur
    public synchronized SerializableList<Tuple<TYPE,Integer>> getStock()
    {
        int i,j ;
        TYPE tmp;
        boolean inclus;
        Tuple<TYPE,Integer> tmpR;
        SerializableList<Tuple<TYPE,Integer>> RepL = new SerializableList<Tuple<TYPE,Integer>>(); 
        for( i = 0; i < RList.size() ; i++)
        {
            inclus = false;
            tmp = RList.get(i).getStockType();
            for ( j = 0; j < RepL.size() ; j++)
            {
                tmpR = RepL.get(j);
                if(tmpR.x == tmp) // Type déjà présent dans la liste
                {
                    tmpR.y = tmpR.y + RList.get(i).getStock();
                    RepL.set( j, tmpR);
                    inclus = true;
                    break;
                }
            }
            if( ! inclus) // Ressource pas encore dans la liste -> l'ajoute
            {
                RepL.add( new Tuple<TYPE,Integer>(tmp, RList.get(i).getStock()));
            }
        }
        
        return RepL;
    }

    public synchronized void decreaseRessourceAmount(int ressource, int x)
        throws RemoteException 
    {
        RList.get(ressource).decreaseRessource(x);
    }
    
    public void fonctionThread ( int ms)
    {
        final int time = ms;
        t = new Thread()
        {
            public void run()
            {
                int i;
                while(true)
                {
                    synchronized(ObjSynchro) // synchronized sert à faire des sections critiques ( section exécutée de façon atomique )
                    {

                        System.out.print("Ajout de ressources. Stock actuel :");
                        int q=0;
                        for(i = 0 ; i< RList.size() ; i++)
                        {
                            q= RList.get(i).getStock();
                            q= Math.min(q/2 + 1, 10000);
                            RList.get(i).increaseRessource(q);
                            System.out.print(" " + RList.get(i).getStockType() + " : " + RList.get(i).getStock());
                        }
                        System.out.println("");
                    }
                    try { Thread.sleep(time); }
                        catch (InterruptedException re) { System.out.println(re) ; System.exit(0);};
                }
            }
        };
        t.start();
    }
    
    public int getStock(int quantity, TYPE T)
        throws RemoteException 
    {
        if (quantity > 10)
            quantity =10;
        
        // Commence par compter le nombre de ressources de ce type chez ce producteur 
        int nType = 0, total = 0, takenRessources = 0 , i;
       // int RNonDivisibles; // non utilisé
        ArrayList<Ressource> RL = new ArrayList<Ressource>(); // y met les ressources de ce type
        
        synchronized (ObjSynchro) // synchronized sert à faire des sections critiques ( section exécutée de façon atomique )
        {
            for (i = 0 ; i < RList.size() ; i++)
            {
                if(RList.get(i).getStockType() == T)
                {
                    nType++;
                    RL.add(RList.get(i));
                    total +=RList.get(i).getStock();
                }
            }
            if(nType == 0)
            {
                System.out.println("On me demande des ressources que je n'ai pas ");
                return 0;
            }
            // on prend des ressources en proportionnelle arrondi a la partie entière
            
            if( total < quantity )  // on a pas assez de ressources -> on transmet ce qu'on a
            {
                for( i=0 ; i < nType ; i ++)
                {
                    System.out.println("\t\t\tJe prends " + RL.get(i).getStock() + " du prod " + i);
                    takenRessources += RL.get(i).takeRessource( RL.get(i).getStock() );
                }
            }
            else
            {
                for( i=0 ; i < nType ; i++)
                {
                    System.out.println("\t\t\tJe prends " + ( (RL.get(i).getStock()  * quantity) / total) + " du prod " + i);
                    takenRessources += RL.get(i).takeRessource( (RL.get(i).getStock()  * quantity) / total);
                }
                i=0;

                // Maintenant il faut chercher le restant des ressources 
                while(takenRessources != quantity)
                {
                    takenRessources += RL.get(i).takeRessource( quantity - takenRessources );
                    i++;
                }
            }
            
            System.out.println("on a " + takenRessources + " et il faut : " + quantity);
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
        }
        return takenRessources;
    }
    
    public int getStockQuantity(TYPE T)
    {
        int i;
        for(i = 0 ; i< RList.size() ;i++)
        {
            if( RList.get(i).getStockType() == T)
                return RList.get(i).getStock();
        }
        return 0;
    }
    
    /**
     * Termine le programme
     */
    public void end()
    throws RemoteException
    {
        System.out.println("J'arrête le producteur");
        t.interrupt();
    }
    
    
}

