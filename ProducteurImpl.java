
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
    static InitialInfoImpl I;
    
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
            I = M.getProducteurInitialInfo(); // demande les informations initiales au contrôleur
            
            
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
    
    // Fonction qui permet de faire un thread qui augmente les ressources du producteur toutes les x ms jusqu'à
    // ce qu'on lui envoie un interrupt
    public void fonctionThread ( int ms)
    {
        final int time = ms;
        t = new Thread()
        {
            public void run()
            {
                while(true)
                {
                    try {addStockTurn();}
                        catch (RemoteException re) { System.out.println(re) ; }
                    try { Thread.sleep(time); }
                        catch (InterruptedException re) { System.out.println("Fin de partie") ; System.exit(0);};
                }
            }
        };
        t.start();
    }
    
    // Fonction qui sert à ajouter des ressources à chaque tour (mode tour / tour et mode temps réel)
    public void addStockTurn()
        throws RemoteException 
    {
        int i;
        synchronized(ObjSynchro) // synchronized sert à faire des sections critiques ( section exécutée de façon atomique )
        {
            System.out.print("Ajout de ressources. Stock actuel :");
            int q=0, ajout;
            for(i = 0 ; i< RList.size() ; i++)
            {
                q= RList.get(i).getStock();
                ajout= q/2 + 5;
                
                if( q >= 10000 )
                    ; // n'ajoute rien
                else if ( 10000 - q < ajout)
                    RList.get(i).increaseRessource( 10000 - q); // devrait théoriquement ajouter plus que ce qu'il reste pour atteindre 10 000,
                                                                // ajoute donc juste ce qui manque pour atteindre 10 000
                else
                    RList.get(i).increaseRessource( Math.min( ajout, 10000 ) ) ; // ajoute les ressources normalement
                System.out.print(" " + RList.get(i).getStockType() + " : " + RList.get(i).getStock());
            }
            System.out.println("");
        }
    }
    
    // Fonction utilisée pour prendre quantitee ressource du type T chez le producteur
    // Lorsqu'un producteur produit plusieurs ressources de même type et qu'on demande cette ressource,
    // le producteur prélève ce montant équitablement sur chaque ressource
    // On peut prendre au maximum une quantité de 10 ressources
    public int getStock(int quantity, TYPE T)
        throws RemoteException 
    {
        if (quantity > 10)
            quantity =10;
        
        // Commence par compter le nombre de ressources de ce type chez ce producteur 
        int nType = 0, total = 0, takenRessources = 0 , i;
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
                    takenRessources += RL.get(i).takeRessource( RL.get(i).getStock() );
                }
            }
            else
            {
                for( i=0 ; i < nType ; i++)
                {
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
    
    // Renvoie la quantité de la ressource T qu'à le producteur
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
        if( I.playMode != 0 ) 
            t.interrupt();
        else
        {
            Thread t1 = new Thread()
            {
                public void run()
                {
                    try {Thread.sleep( 1000 );}
                        catch (InterruptedException re) { System.out.println(re) ; }
                    System.exit( 0 );
                }
            };
            t1.start();
        }
    }
    
    
}

