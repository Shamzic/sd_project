import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.concurrent.TimeUnit;


class JoueurImpl extends UnicastRemoteObject implements Joueur
{
	public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
    public static int id, RD, RI;
    public static SerializableList<Ressource> RList;
    static ConnexionImpl C;
    static MessageControle M;
    Thread T ;
    final static Object monitor = new Object();
    static JoueurImpl J;
    static boolean have_token = false;
    public COMPORTEMENT comportement= COMPORTEMENT.VOLEUR;

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
            
            TripleImpl T =M.getPlayerInitialInfo();
            System.out.println("Le joueur reçoit l'id : " + T.x + ", RI : " + T.y + ", RD : " + T.z);
            
            // initialise le serveur joueur
            J = new JoueurImpl (T.x, T.y, T.z, args[3]);
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
     */
	JoueurImpl(int id, int RI, int RD, String portSelf)
    throws RemoteException
	{
		JoueurImpl.id = id;
        JoueurImpl.RD = RD; // Ressources différentes ??
        JoueurImpl.RI = RI; // Ressources initiales du joueur, inutile ???
        RList = new SerializableList<Ressource> (RD);
        // Boucle pour init la liste des ressources du joueur
        // Ayant des quantité nulles pour chaque type
        for (TYPE t : TYPE.values())
        {
        	RList.add(new Ressource(0,t));
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
        while(true)
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
  //                              debut = false;
                            }
                        }
                        //System.out.println("je prend des ressources " + C.PList.get(0).getStock( 10 , TYPE.OR));
                        //System.out.println("je prend des ressources " + C.PList.get(0).getStock( 10 , TYPE.BOIS));
                        int ressources_prises = C.PList.get(0).getStock( 9 , TYPE.OR);
                        System.out.println("Je prend "+ressources_prises+" ressources d'or !");
                       	increaseRessourceAmout(TYPE.OR,ressources_prises);
                        /* Affichage des ressources du joueur */
                        displayRessourceList();
                        
                        M.sendInformation(id, RList);
                        TimeUnit.SECONDS.sleep(3);

                        if(C.JList.size() != 0) // besoin car sinon division par 0 et ça fait tout planter
                        {
                            System.out.println("Envoie token a " + ((id +1) %C.JList.size()) );
                            have_token = false;
                            C.JList.get((id +1) %C.JList.size()).receiveToken();
                     	}

                    }
                    catch (InterruptedException re) { System.out.println(re) ; }
                    catch (RemoteException re) { System.out.println(re) ; }
                }
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
     * Confirmation de l'ajout du joueur
     *
     */
    public void salut()
    throws RemoteException
    {
        System.out.println("Salut joueur"+id+", on vient de t'ajouter");
    }
    
}

