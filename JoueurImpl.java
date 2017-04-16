import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.concurrent.TimeUnit;


class JoueurImpl extends UnicastRemoteObject implements Joueur
{
	public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    public static int id;
    public static SerializableList<Ressource> RList;
    static ConnexionImpl C;
    static MessageControle M;
    Thread T ;
    final static Object monitor = new Object();
    static JoueurImpl J;
    static boolean have_token = false;
    InitialInfoImpl I;
    boolean game = true;
    //public COMPORTEMENT comportement= COMPORTEMENT.VOLEUR;

    /**
	 *
	 * Méthode principale qui lance un joueur lié à un producteur
	 * par leurs numéros de ports.
	 *
	 * @arg0 
	 *		Nom de machine du controleur
	 * @arg1 
	 *		Numéro de port du controleur
	 * @arg2 
	 *		Nom de machine du joueur
	 * @arg3 
	 *		Numéro de port du producteur
     */
    public static void main (String [] args)
    {
        if ( args.length != 4)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <JoueurMachineName> <ProducterPort>");
            System.exit(1);
        }
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            
            InitialInfoImpl I =M.getPlayerInitialInfo();
            System.out.println("Le joueur reçoit l'id : " + I.IdJoueur + ", RI : " + I.nbRessourcesInitiales + ", RD : " + I.nbRessourcesDifferentes);
            id = I.IdJoueur;
            // initialise le serveur joueur
            J = new JoueurImpl (I,args[3]);
            Naming.rebind( "rmi://localhost:"+args[3] + "/Joueur", J);
            
            // Maintenant envoie ses "coordonnées" au Coordinateur
            M.addMachine( args[2], Integer.parseInt(args[3]) );

            J.start();
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        catch (NotBoundException re) { System.out.println(re) ; }
        
        
    }
    
    /**
     * Constructeur de la classe JoueurImpl
     *
     * @param id
     * 			  Le numéro de port du joueur
     * @param RI
     *            Les ressources initiales. // inutile
     * @param RD
     *            Le nombre de ressources différentes possibles
     * @param portSelf
     *            Le numéro de port du joueur
     * @param victory_condition
     *            Définie le mode de jeu : 0 :arrêt du jeu dès le premier
     *            qui a atteint le but, 1 : chaque joueur s'arrête quand
     *            il a atteint le but
     */
	JoueurImpl( InitialInfoImpl I, String portSelf)
    throws RemoteException
	{
		this.id = id;
        this.I = I;
        
        RList = new SerializableList<Ressource> (I.nbRessourcesDifferentes);
        // Boucle pour init la liste des ressources du joueur
        // Ayant des quantité nulles pour chaque type
        for (TYPE t : TYPE.values())
        {
        	RList.add(new Ressource(0,t));
        }

        // Init comportement random
        Random randomitude = new Random();
        switch(randomitude.nextInt(3))
        {
            case 0:
            {
                T = TYPE.Coopératif;
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

        // initialise le serveur connexion pour que le controlleur puisse lui envoyer les nouveaux connectés
        C = new ConnexionImpl();
        try
        {
            Naming.rebind( "rmi://localhost:"+ portSelf + "/Connexion", C);
        }
        catch (RemoteException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
                
    }
	
	/**
     * Incrémente de x la quantité de la ressource de type t
     * 
     * @param t
     * 			  Le type de la ressource.
     * @param x
     *            La quantité de ressource à augmenter.
     */
	public void increaseRessourceAmout(TYPE t, int x)
    throws RemoteException
    {
    	for(int i=0 ; i < RList.size() ; i++)
           if(RList.get(i).getStockType()==t)
           		RList.get(i).increaseRessource(x);
    }

    /**
     * Exécute les tâches du joueur chaque tour
     * Celui ci prend chaque tour des ressources d'or
     *
     */
    public  void start()
    throws RemoteException
    {
//        boolean debut = true;
        while(game)
        {
            try
            {
                synchronized (monitor)
                {
                    if ( C.JList.size() != 0 )//&& debut) // besoin car sinon division par 0 et ça fait tout planter
                    {
                        while ( have_token == false)
                        {
                            monitor.wait(100);
                        }
  //                    debut = false;
                    }
                }
                /*****************************************
                 * Ici on va implémenter les comportements
                 *****************************************
                  - Individualiste : 
                    Prend autant de ressources que possible pour atteindre l'objectif (10 max par tour).

                    -> Demande au premier producteur si il a les ressources nécessaires...
                        - Si oui, alors il se sert comme un rat et en prend 10.
                        - Si non, alors il demande au producteur suivant.
                            - puis il termine son tour.

                    -----------------------------------------------------

                    -Voleur :
                    Observe les joueurs et leur vole des ressources.

                    -> Demande au premier joueur si il a des ressources nécessaire pour gagner
                        - Si oui alors il les prends (15 max par tour) 
                        - Si non alors il observe le joueur suivant
                             - si après avoir parcouru tous les joueurs il n'y en a aucun qu'on peut voler
                               alors le voleur devient un individualiste pour 1 tour puis redevient voleur.
                    - puis il termine son tour et recommence

                    -----------------------------------------------------
                    
                    -Coopératif
                    Observe les joueurs et prends des ressources si le producteur en as produit au moins
                    la moitié du nombre de ressources à atteindre

                    -> observe un producteur 
                        - si il a au moins la moitié des ressources nécessaire alors il se sert (10 max)
                        - si le producteur en a , mais pas assez alors il attend
                        - si le producteur n'en produit pas alors il passe au producteur suivant
                            - fin du tour

                     -----------------------------------------------------

                    -Traitre
                    Mode coopératif et puis devient voleur

                    -> observe un producteur 
                        - si il a au moins la moitié des ressources nécessaire alors il se sert (10 max)
                          puis passe en mode voleur
                        - si le producteur en a, mais pas assez alors il attend
                        - si le producteur n'en produit pas alors il passe au producteur suivant
                            - fin du tour

                 */


                //System.out.println("je prend des ressources " + C.PList.get(0).getStock( 10 , TYPE.BOIS));

                // ici il devrait prendre un nombre aléatoire (entre 1 et 10 par ex) de ressource de type aléatoire
                int ressources_prises = C.PList.get(0).getStock( 9 , TYPE.OR);
                System.out.println("Je prend "+ressources_prises+" ressources d'or !");
                increaseRessourceAmout(TYPE.OR,ressources_prises);
                /* Affichage des ressources du joueur */
    //          displayRessourceList();
                
                M.sendInformation(id, RList);
                
                TimeUnit.SECONDS.sleep(3);
                // test de victoire et passe le jeton
                victory_test();
            }
            catch (InterruptedException re) { System.out.println(re) ; }
            catch (RemoteException re) { System.out.println(re) ; }
        }
    }

    
    // test de fin de jeu
    public void victory_test()
    {
        int i;
        System.out.println("je rentre dans le test");
        try
        {
            if( have_win() )
            {
                System.out.println("J'AI GAGNÉ");
                if(I.victory_condition == 0) // dès le premier vainqueur arrête le jeu
                {
                    C.endAllAgents(id); // termine tous les agents
                    M.GameEnded(); // envoie au coordinateur l'information que le jeu est terminé
                    return;
                }
                else // termine le jeu progressivement
                {
                    
                }

            }


            if(C.JList.size() != 0) // besoin car sinon division par 0 et ça fait tout planter
            {
                System.out.println("Envoie token a " + ((id +1) %C.JList.size()) );
                have_token = false;
                C.JList.get((id +1) %C.JList.size()).receiveToken();
            }
        }
        catch (RemoteException re) { System.out.println(re) ; }
        
    }
    
    // test si a remplit les conditions nécessaires pour gagner
    public boolean have_win()
    {
        int i,j;
        Ressource RWIN, RJ;
        SerializableList<Ressource> LWIN = I.VLC ;
        for( i = 0 ; i < LWIN.size() ; i++ )
        {
            RWIN = I.VLC.get(i);
            for ( j = 0 ; j < RList.size() ; j++ )
            {
                RJ = RList.get(i);
                if( RJ.getStockType() == RWIN.getStockType() )
                {
                    System.out.println("je fais le test de ressource pour gagner, il me faut " + RWIN.getStock() + " et j'ai " + RJ.getStock());
                    if( RJ.getStock() < RWIN.getStock() ) // pas assez de ressources
                        return false;
                    break; // passe à la prochaine ressource RWIN
                }
            }
        }
        return true;
    }
    
    
    /**
     * Demande une quantité de ressource d'un certains type à un producteur
     * 
     * @param productorNumber
     *            Le numero du producteur.
     * @param t
     *            Le type de la ressource.
     * @param quantity
     *            La quantité de ressource à prendre.
     */
    public void askProdForRessource(int productorNumber, TYPE t, int quantity)
    throws RemoteException
    {
    	 int ressources_prises = C.PList.get(productorNumber).getStock(quantity,t);
    	 if(ressources_prises>0)
    	 	System.out.println(""+ressources_prises+" ressources d'or prises au producteur "+productorNumber);
    	 else
    	 	System.out.println("Le producteur"+productorNumber+" n'as pas pu fournir de ressources "+t);
    }
    
    /**
     * Affichage les ressources possédées par le joueur
     * avec leur types et leur quantités.
     *
     */
    public void displayRessourceList()
    throws RemoteException
    {
    	System.out.println("******* ETAT DES RESSOURCES JOUEUR *******");
        for(int i=0;i<RList.size();i++)
            System.out.println(RList.get(i).toString());	
		System.out.println("******************************************");
	}


	/**
     * Confirmation de la réception du jeton.
     *
     * Met à jour le booléen et notifie le monitor
     * pour lancer le tour du joueur.
     *
     */
    public void receiveToken()
    throws RemoteException
    {
        System.out.println("J'ai eu le token");
        have_token = true;
        synchronized(monitor)
        {monitor.notify();}
    }
    
    /**
     * Termine le programme
     */
    public void end()
    throws RemoteException
    {
        System.out.println("Je m'arrête");
        game = false;
    }
}

