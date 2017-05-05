import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.util.concurrent.TimeUnit;
import java.util.Random ;



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
    public COMPORTEMENT comportement= COMPORTEMENT.COOPERATIF;
    public ETAT etat = ETAT.ATTEND;
    static Object ObjSynchro = new Object();
    boolean played = false;
    
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
        if ( args.length != 5)
        {
            System.err.println( "usage : <ControllerMachineName> <ControllerPort> <JoueurMachineName> <ProducterPort> <comportement>");
            System.exit(1);
        }
        try
		{
            
            // débute la communication avec le controller
            M = (MessageControle) Naming.lookup("rmi://" + args[0] + ":" + args[1] + "/MessageControleGlobal");
            
            InitialInfoImpl I =M.getPlayerInitialInfo();

            id = I.IdJoueur;
            // initialise le serveur joueur
            J = new JoueurImpl (I,args[3],args[4]);
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

	JoueurImpl( InitialInfoImpl I, String portSelf,String comportement)
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

        // Init comportement
        switch(comportement)
        {
            case "cooperatif":
            {
                this.comportement = COMPORTEMENT.COOPERATIF;
                System.out.println("Comportement du joueur : coopératif.");
                break;
            }
            case "individualiste":
            {
                this.comportement = COMPORTEMENT.INDIVIDUALISTE;
                System.out.println("Comportement du joueur : individualiste.");
                break;
            }
            case "voleur":
            {
                this.comportement = COMPORTEMENT.VOLEUR;
                System.out.println("Comportement du joueur : voleur.");
                break;
            }
            case "attentionnel":
            {
                this.comportement = COMPORTEMENT.ATTENTIONNEL;
                System.out.println("Comportement du joueur : attentionnel.");
                break;
            }
            case "brute":
            {
                this.comportement = COMPORTEMENT.BRUTE;
                System.out.println("Comportement du joueur : brute.");
                break;
            }
            case "humain":
            {
                this.comportement = COMPORTEMENT.HUMAIN;
                System.out.println("Comportement du joueur : humain.");
                break;
            }
            
            default: 
            {
                this.comportement = COMPORTEMENT.COOPERATIF;
                System.out.println("Comportement par défaut du joueur : coopératif." + comportement);
                break;
            }
        }

        // initialise le serveur connexion pour que le controlleur puisse lui envoyer les nouveaux connectés
        C = new ConnexionImpl();
        System.out.println("je me suis connecté au port" + portSelf);
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

        int parcours_prod=0;
        int parcours_joueurs=0;
        boolean begin = true;
        while(game)
        {
            try
            {
                if (I.playMode == 0 || begin) // mode tour/tour
                {
                    synchronized (monitor)
                    {
                        if ( C.JList.size() != 0 )// besoin car sinon division par 0 et ça fait tout planter
                        {
                            while ( have_token == false)
                            {
                                monitor.wait(100);
                                if(!game)
                                {
                                    try {Thread.sleep( 1000 );}
                                        catch (InterruptedException re) { System.out.println(re) ; }
                                    System.exit( 0 );
                                }
                            }
                        }
                    }
                    begin = false;
                }
                
                
                if( etat == ETAT.PENALITE3) // c'est fait prendre en volant au tour d'avant
                {
                    System.out.println("Je dois attendre ce tour");
                    etat = ETAT.PENALITE2;
                    if( I.playMode == 1 ) 
                        try {Thread.sleep( 50 );}
                            catch (InterruptedException re) { System.out.println(re) ; }                
                }
                if( etat == ETAT.PENALITE2) // c'est fait prendre en volant au tour d'avant
                {
                    System.out.println("Je dois attendre ce tour");
                    etat = ETAT.PENALITE;
                    if( I.playMode == 1 ) 
                        try {Thread.sleep( 50 );}
                            catch (InterruptedException re) { System.out.println(re) ; }                
                }
                else if( etat == ETAT.PENALITE) // c'est fait prendre en volant au tour d'avant
                {
                    System.out.println("Je dois attendre ce tour");
                    etat = ETAT.ATTEND;
                    if( I.playMode == 1 ) 
                        try {Thread.sleep( 50 );}
                            catch (InterruptedException re) { System.out.println(re) ; }                
                }
                else
                {
                    // Maintenant on a le jeton et on peut jouer=> Comportement joueur activé !

                    if(this.comportement == COMPORTEMENT.INDIVIDUALISTE)
                    {
                        comportement_individualiste();
                    }

                    else if(this.comportement == COMPORTEMENT.COOPERATIF)
                    {
                        comportement_cooperatif(1.5);  
                    }

                    else if(this.comportement == COMPORTEMENT.VOLEUR)
                    {
                        comportement_voleur();
                    }
                    else if(this.comportement == COMPORTEMENT.ATTENTIONNEL)
                    {
                        comportement_attentionnel();
                    }
                    else if(this.comportement == COMPORTEMENT.BRUTE)
                    {
                        comportement_attack();
                    }
                    else
                    {
                        System.out.println("Je suis un humain !!");
                    }
                }


                System.out.println(". Tour joueur terminé en état "+etat);
                M.sendInformation(id, RList);
                TimeUnit.SECONDS.sleep(1);
                    
                // test de victoire et passe le jeton
                victory_test();
                if( C.JList.size() == C.FinishedPlayerList.size() ) // Tous on fini 
                {
                    C.endAllProducteurs();
                    return ;
                }
            }
            catch (InterruptedException re) { System.out.println(re) ; }
            catch (RemoteException re) { System.out.println(re) ; }
        }
        try {Thread.sleep( 1000 );}
            catch (InterruptedException re) { System.out.println(re) ; }
        System.exit( 0 );
        
    }


    /*****************************************
    * Ici on va implémenter les comportements
    *****************************************
    - Individualiste : 
    * Recherche en priorité la ressource qui existe le moins (et qui sera donc le moins produite)
    * Prend le max qu'il a besoin pour cette ressource
    * Fait un calcul pour savoir quelle ressource il va prendre en priorité : il compte la 
    * quantité disponible pour chaque ressource et le nombre de producteurs qui la produisent
    * puis fait le calcul importance = RessourceQuantitée + nbProducteurs * multiplicateur
    * avec multiplicateur un nombre utilisé pour donner de l'importance au nombre de producteurs produisants cette ressource
    * Recherche en priorité la ressource avec le facteur le plus petit
    * Ces calculs ne sont effectués que sur les ressources dont les conditions de victoire n'ont pas encore été remplies

    ---------------------------------------------------
    */
    void comportement_individualiste()
    {
        int i,j,k;
        int ressources_prises = 0, needed_ressources = 0;
        boolean inclus;
        int min =-1, max ,index = 0, index2 = 0, tmp;
        // débute par chercher la liste des ressources ainsi que leurs nombre chez les producteurs
        SerializableList< SerializableList < Tuple<TYPE,Integer> > > L  = new SerializableList< SerializableList < Tuple<TYPE,Integer> > >() ; // Pour chaque producteur, liste des types et quantitée de ressources
        try
        {
            for (i=0 ; i < C.PList.size() ; i++)
                L.add( C.PList.get(i).getStock() );
        }  catch (RemoteException re) { System.out.println(re) ; }
        
        // débute par faire une liste des ressources qu'on a déjà
        ArrayList<TYPE> RHave = new ArrayList<TYPE>();
        for (i=0 ; i < I.VLC.size() ; i++)
        {
            if( RList.get(i).getStock() >= I.VLC.get(i).getStock() )
            {
                RHave.add(I.VLC.get(i).getStockType());
                System.out.println("\t\tRécolte de " + I.VLC.get(i).getStockType() + " Terminée");
            }
        }
        
        
        // Maintenant compte le nombre de ressource pour chaque type de ressource ainsi que les montants
        SerializableList<Tuple<TYPE,Integer> > TypeNb = new SerializableList< Tuple<TYPE,Integer> > (); // Liste contenant les types et le nombre de ressources de ce type
        SerializableList<Tuple<TYPE,Integer> > TypeQuantite = new SerializableList< Tuple<TYPE,Integer> > (); // Liste contenant les types et la quantité  de ressource de tous les prod réunis
        for(i = 0 ; i < L.size(); i++) // Pour chaque producteur
        {
            for(j=0; j < L.get(i).size() ; j++) // Pour chaque ressource du producteur
            {
                Tuple<TYPE,Integer> Ress = L.get(i).get(j); // la ressource en question
                if( RHave.contains(Ress.x) ) // évite de prendre en compte des ressources qu'on a déjà
                    continue;
                
                inclus = false;
                for (k = 0 ; k < TypeNb.size() ; k++) // pour chaque ressource de la liste TypeNb
                {
                    if( Ress.x == TypeNb.get(k).x) // type de ressource déjà dans la liste
                    {
                        TypeNb.set( k,new Tuple<TYPE,Integer>( Ress.x, TypeNb.get(k).y +1 )) ;
                        TypeQuantite.set(k, new Tuple<TYPE,Integer>( Ress.x, TypeQuantite.get(k).y + Ress.y )) ;
                        inclus = true;
                        break;
                    }
                }
                if( !inclus ) // ressource pas encore dans liste
                {
                    TypeNb.add( new Tuple<TYPE,Integer>( Ress.x, 1) );
                    TypeQuantite.add( new Tuple<TYPE,Integer>( Ress.x,Ress.y));
                }
                
            }
        }
        
        if(TypeNb.size()==0)
        {
            System.out.println("ATTENTION : il n'y a plus de ressources");
            etat=ETAT.ATTEND;
            return;
        }
        // Maintenant doit décider ce qu'il veut prendre comme ressource
        // Cherche d'abord à avoir la ressource avec le moins de producteur ( multiplicateur d'importance *10) et le moins de ressource 
        SerializableList<Tuple<TYPE,Integer>> PriorityList = new SerializableList<Tuple<TYPE,Integer> >();
        boolean inserted = false;
        for (i=0 ; i< TypeNb.size() ; i++)
        {
            tmp = TypeNb.get(i).y * 10 + TypeQuantite.get(i).y;
            if( PriorityList.size() == 0) // liste vide
                PriorityList.add( new Tuple<TYPE,Integer> ( TypeNb.get(i).x, tmp));
            else
            {
                inserted =false ;
                for(j=0; j < PriorityList.size() ; j++)
                {
                    if( PriorityList.get(j).y > tmp ) // nombre plus grand que tmp
                    {
                        PriorityList.add( new Tuple<TYPE,Integer>( TypeNb.get(i).x, tmp) , j);
                        inserted = true;
                        break;
                    }
                }
                if( !inserted )
                    PriorityList.add( new Tuple<TYPE,Integer>( TypeNb.get(i).x, tmp)); // tmp est le plus grand de la liste
            }
            
        }
        Random r = new Random();
        index = r.nextInt( Math.min(2,PriorityList.size()) ); // choisit soit la première ressource, soit la deuxième, s'il reste une deuxième ressource à chercher

        // On a choisi la ressource à la position i de la liste TypeNb
        max = -1;
        // Maintenant il faut choisir le producteur chez qui on prend les ressources -> celui qui en a le plus
        for(i=0 ; i < L.size() ; i++)
        {
            for(j=0; j < L.get(i).size() ; j++)
            {
                Tuple<TYPE,Integer> Res = L.get(i).get(j);
                if( Res.x == TypeNb.get(index).x ) // ressource de type de la ressource qu'on recherche
                {
                    if(max == -1)
                    {
                        max = Res.y;
                        index2 = i;
                    }
                    else if( Res.y > max )
                    {
                        max = Res.y;
                        index2 = i;
                    }
                    break;
                }
            }
        }
        
        // Calcul le nombre de ressources qu'il faut pour gagner
        for(i=0 ; i < I.VLC.size() ; i++)
        {
            if(  I.VLC.get(i).getStockType() == TypeNb.get(index).x )
            {
                needed_ressources =  I.VLC.get(i).getStock();
                break;
            }
        }
        for(i=0 ; i < RList.size() ; i++)
        {
            if( RList.get(i).getStockType() == TypeNb.get(index).x )
            {
                needed_ressources -= RList.get(i).getStock();
                break;
            }
        }
        
        
        // cherche la ressource chez le producteur
        try
        {
            ressources_prises = C.PList.get(index2).getStock( needed_ressources , TypeNb.get(index).x) ;
        }catch (RemoteException re) { System.out.println(re) ; }
                    
        if( ressources_prises > 0) // le producteur a des ressources
        {
            etat = ETAT.PREND_RESSOURCES;
            System.out.print("Je prends "+ressources_prises+" ressources de " + TypeNb.get(index).x +" au producteur n°"+index2);
            try
            {
                increaseRessourceAmout( TypeNb.get(index).x,ressources_prises);
            } catch (RemoteException re) { System.out.println(re) ; }
        }
        else
        {
            System.out.print("Pas assez de ressource disponibles");
            etat=ETAT.ATTEND;
        }
        
    }
    


    /*
    -Coopératif
    Attend qu'il y ai assez * multiplicateur ressources pour tous le monde avant de prendre quoi que ce soit
    * Pour cela calcule à chaque tour l'ensemble des ressources des producteurs et des joueurs
    * Puis regarde si leurs somme est supérieur à multiplicateur x la quantité de ressource nécessaire pour
    * que l'ensemble des joueurs puissent gagner la partie
    -----------------------------------------------------
    */
    void comportement_cooperatif(double multiplicateur)
    {
        int i,j,k;
        int ressources_prises = 0, needed_ressources = 0;
        boolean find = false, inclus =false;
        int min =-1, max , tmp;
        // débute par chercher la liste des ressources ainsi que leurs nombre chez les producteurs
        SerializableList< SerializableList < Tuple<TYPE,Integer> > > L  = new SerializableList< SerializableList < Tuple<TYPE,Integer> > >() ; // Pour chaque producteur, liste des types et quantitée de ressources
        SerializableList< SerializableList < Tuple<TYPE,Integer> > > LJoueurs  = new SerializableList< SerializableList < Tuple<TYPE,Integer> > >() ; // Pour chaque joueur, liste des types et quantitée de ressources
        TYPE seekedType = TYPE.OR;
        try
        {
            for (i=0 ; i < C.PList.size() ; i++)
                L.add( C.PList.get(i).getStock() );
        }  catch (RemoteException re) { System.out.println(re) ; }
        
        // débute par faire une liste des ressources qu'on a déjà
        ArrayList<TYPE> RHave = new ArrayList<TYPE>();
        for (i=0 ; i < I.VLC.size() ; i++)
        {
            if( RList.get(i).getStock() >= I.VLC.get(i).getStock() )
            {
                RHave.add(I.VLC.get(i).getStockType());
                System.out.println("\t\tRécolte de " + I.VLC.get(i).getStockType() + " Terminée");
            }
        }
        
        
        // Maintenant compte le nombre de ressource pour chaque type de ressource ainsi que les montants
        SerializableList<Tuple<TYPE,Integer> > TypeQuantiteP = new SerializableList< Tuple<TYPE,Integer> > (); // Liste contenant les types et la quantité  de ressource de tous les prod réunis
        for(i = 0 ; i < L.size(); i++) // Pour chaque producteur
        {
            for(j=0; j < L.get(i).size() ; j++) // Pour chaque ressource du producteur
            {
                Tuple<TYPE,Integer> Ress = L.get(i).get(j); // la ressource en question
                if( RHave.contains(Ress.x) ) // évite de prendre en compte des ressources qu'on a déjà
                    continue;
                
                inclus = false;
                for (k = 0 ; k < TypeQuantiteP.size() ; k++) // pour chaque ressource de la liste TypeNb
                {
                    if( Ress.x == TypeQuantiteP.get(k).x) // type de ressource déjà dans la liste
                    {
                        TypeQuantiteP.set(k, new Tuple<TYPE,Integer>( Ress.x, TypeQuantiteP.get(k).y + Ress.y )) ;
                        inclus = true;
                        break;
                    }
                }
                if( !inclus ) // ressource pas encore dans liste
                {
                    TypeQuantiteP.add( new Tuple<TYPE,Integer>( Ress.x,Ress.y));
                }
                
            }
        }
        
        if(TypeQuantiteP.size()==0)
        {
            System.out.println("ATTENTION : il n'y a plus de ressources");
            etat=ETAT.ATTEND;
            return;
        }        
        
        // Maintenant recherche les ressources chez les joueurs
        try
        {
            for (i=0 ; i < C.JList.size() ; i++)
                LJoueurs.add( C.JList.get(i).getStock() );
        }  catch (RemoteException re) { System.out.println(re) ; }
        
        SerializableList<Tuple<TYPE,Integer> > TypeQuantiteJ = new SerializableList< Tuple<TYPE,Integer> > (); // Liste contenant les types et la quantité  de ressource de tous les joueurs réunis
        for(i = 0 ; i < LJoueurs.size(); i++) // Pour chaque producteur
        {
            for(j=0; j < LJoueurs.get(i).size() ; j++) // Pour chaque ressource du producteur
            {
                Tuple<TYPE,Integer> Ress = LJoueurs.get(i).get(j); // la ressource en question
                if( RHave.contains(Ress.x) ) // évite de prendre en compte des ressources qu'on a déjà
                    continue;
                
                inclus = false;
                for (k = 0 ; k < TypeQuantiteJ.size() ; k++) // pour chaque ressource de la liste 
                {
                    if( Ress.x == TypeQuantiteJ.get(k).x) // type de ressource déjà dans la liste
                    {
                        TypeQuantiteJ.set(k, new Tuple<TYPE,Integer>( Ress.x, TypeQuantiteJ.get(k).y + Ress.y )) ;
                        inclus = true;
                        break;
                    }
                }
                if( !inclus ) // ressource pas encore dans liste
                {
                    TypeQuantiteJ.add( new Tuple<TYPE,Integer>( Ress.x,Ress.y));
                }
                
            }
        }
        
        // Maintenant choisi la ressource qu'il voudra prendre
        // regarde si on a atteint <Total des ressources des producteurs> + <Total des ressources des joueurs>* multiplicateur > multiplicateur * <Ressources Nécessaires Victoire> * nbJoueurs
        // On multiplie par multiplicateur car il y a plusieurs producteurs ->  augmente les chances qu'un joueur ne doit pas aller chercher ses ressources chez 2-3 producteurs différents
        for( i = 0; i <TypeQuantiteP.size() ; i++) // pour chaque ressource
        {
            if( TypeQuantiteP.get(i).y + (TypeQuantiteJ.get(i).y * multiplicateur) > multiplicateur * I.getStockQuantity( TypeQuantiteP.get(i).x )  * I.nbJoueurs ) // s'il y en a assez
            {
                if ( getStockQuantity( TypeQuantiteP.get(i).x ) < I.getStockQuantity( TypeQuantiteP.get(i).x ) ) // si la ressource n'est pas encore acquise
                    seekedType = TypeQuantiteP.get(i).x ;
            }
        //    System.out.println("\tIl faut "+ (multiplicateur * I.getStockQuantity( TypeQuantiteP.get(i).x )  * I.nbJoueurs) +" et j'ai " + (TypeQuantiteP.get(i).y + (TypeQuantiteJ.get(i).y * multiplicateur) )+ " de type "  +TypeQuantiteP.get(i).x  );
        }
        
        // Maintenant cherche le premier producteur chez qui prendre la ressource
        for(i=0;i< L.size() ; i++)
        {
            for(j=0;j<L.get(i).size() ; j++)
            {
                Tuple<TYPE,Integer> Ress = L.get(i).get(j);
                needed_ressources = I.getStockQuantity( seekedType) - getStockQuantity(seekedType);
                if( Ress.x == seekedType && Ress.y > needed_ressources ) // bon type et assez de ressources
                {
                    try
                    {
                        ressources_prises = C.PList.get(i).getStock( needed_ressources , seekedType) ;
                    }catch (RemoteException re) { System.out.println(re) ; }
                    find = true;
                    break;
                }
            }
            if(find)
                break;
        }
        
        if( ressources_prises > 0) // C'est bon on prend des ressources
        {
            etat = ETAT.PREND_RESSOURCES;
            System.out.print("Je prends "+ressources_prises+" ressources de " + seekedType +" au producteur n°"+ i);
            try
            {
                increaseRessourceAmout( seekedType,ressources_prises);
            } catch (RemoteException re) { System.out.println(re) ; }
        }
        else
        {
            System.out.print("Pas assez de ressources disponibles");
            etat=ETAT.ATTEND;
        }
        
            
    }


    /* -Voleur :

    Observe les joueurs et leur vole des ressources.
    * Regarde d'abord ce que chaque joueur possède
    * Regarde s'il y en a un qui est proche de la fin
    *   -> Si oui -> le vole en priorité (la ressource la plus rare)
    * Sinon cherche la ressource la plus rare chez les joueurs (comme avec les producteurs)

    ----------------------------------------------------- */
    void comportement_voleur()
    {
        int Res =0, index,i,j,k, needed_ressources = 0;
        boolean inclus;
        int max, ressources_prises =0, tmp;
        TYPE choosenType;
        ArrayList<TYPE> RHave = new ArrayList<TYPE>();
        SerializableList< SerializableList < Tuple<TYPE,Integer> > > LJoueurs  = new SerializableList< SerializableList < Tuple<TYPE,Integer> > >() ; // Pour chaque joueur, liste des types et quantitée de ressources
        SerializableList<Tuple<TYPE,Integer> > TypeQuantiteJ = new SerializableList< Tuple<TYPE,Integer> > (); // Liste contenant les types et la quantité  de ressource de tous les joueurs réunis
        
        // débute par faire une liste des ressources qu'on a déjà
        for (i=0 ; i < I.VLC.size() ; i++)
        {
            if( RList.get(i).getStock() >= I.VLC.get(i).getStock() )
            {
                RHave.add(I.VLC.get(i).getStockType());
                System.out.println("\t\tRécolte de " + I.VLC.get(i).getStockType() + " Terminée");
            }
        }
        
        // Maintenant recherche les ressources chez les joueurs
        try
        {
            for (i=0 ; i < C.JList.size() ; i++)
                LJoueurs.add( C.JList.get(i).getStock() );
        }  catch (RemoteException re) { System.out.println(re) ; }
        
        for(i = 0 ; i < LJoueurs.size(); i++) // Pour chaque joueur
        {
            if(i== I.IdJoueur) // ne se compte pas soit même
                continue;
            for(j=0; j < LJoueurs.get(i).size() ; j++) // Pour chaque ressource du joueur
            {
                Tuple<TYPE,Integer> Ress = LJoueurs.get(i).get(j); // la ressource en question
                if( RHave.contains(Ress.x) ) // évite de prendre en compte des ressources qu'on a déjà
                    continue;
                
                inclus = false;
                for (k = 0 ; k < TypeQuantiteJ.size() ; k++) // pour chaque ressource de la liste 
                {
                    if( Ress.x == TypeQuantiteJ.get(k).x) // type de ressource déjà dans la liste
                    {
                        TypeQuantiteJ.set(k, new Tuple<TYPE,Integer>( Ress.x, TypeQuantiteJ.get(k).y + Ress.y )) ;
                        inclus = true;
                        break;
                    }
                }
                if( !inclus ) // ressource pas encore dans liste
                {
                    TypeQuantiteJ.add( new Tuple<TYPE,Integer>( Ress.x,Ress.y));
                }
                
            }
        }
        
        // Maintenant doit décider ce qu'il veut prendre comme ressource
        // Cherche d'abord à avoir la ressource avec le moins de joueurs ( multiplicateur d'importance *10) et le moins de ressource 
        SerializableList<Tuple<TYPE,Integer>> PriorityList = new SerializableList<Tuple<TYPE,Integer> >();
        boolean inserted = false;
        for (i=0 ; i< TypeQuantiteJ.size() ; i++)
        {
            tmp = TypeQuantiteJ.get(i).y;
            if( PriorityList.size() == 0) // liste vide
                PriorityList.add( new Tuple<TYPE,Integer> ( TypeQuantiteJ.get(i).x, tmp));
            else
            {
                inserted =false ;
                for(j=0; j < PriorityList.size() ; j++)
                {
                    if( PriorityList.get(j).y > tmp ) // nombre plus grand que tmp
                    {
                        PriorityList.add( new Tuple<TYPE,Integer>( TypeQuantiteJ.get(i).x, tmp) , j);
                        inserted = true;
                        break;
                    }
                }
                if( !inserted )
                    PriorityList.add( new Tuple<TYPE,Integer>( TypeQuantiteJ.get(i).x, tmp)); // tmp est le plus grand de la liste
            }
            
        }
        Random r = new Random();
        index = r.nextInt( Math.min(2,PriorityList.size()) ); // choisit soit la première ressource, soit la deuxième, s'il reste une deuxième ressource à chercher
        choosenType = PriorityList.get( PriorityList.size()-1 ).x;
        
        max = -1;
        // Maintenant il faut choisir le joueur chez qui on prend les ressources -> celui qui en a le plus
        for(i=0 ; i < C.JList.size() ; i++)
        {
            if(i== I.IdJoueur) // ne se compte pas soit même
                continue;
            try
            {
                Res = C.JList.get(i).getStockQuantity( choosenType);
            }catch (RemoteException re) { System.out.println(re) ; }
         
            if(max == -1)
            {
                max = Res;
                index = i;
            }
            else if( Res > max )
            {
                max = Res;
                index = i;
            }
        }
        
        
        
        // Calcul le nombre de ressources qu'il faut pour gagner
        for(i=0 ; i < I.VLC.size() ; i++)
        {
            if(  I.VLC.get(i).getStockType() == choosenType )
            {
                needed_ressources =  I.VLC.get(i).getStock();
                break;
            }
        }
        for(i=0 ; i < RList.size() ; i++)
        {
            if( RList.get(i).getStockType() == choosenType )
            {
                needed_ressources -= RList.get(i).getStock();
                break;
            }
        }
        
        
        // cherche la ressource chez le joueur
        try
        {
            ressources_prises = C.JList.get(index).getStock( needed_ressources , choosenType) ;
        }catch (RemoteException re) { System.out.println(re) ; }
                    
        if( ressources_prises >= 0) // le joueur a donné ressources
        {
            etat = ETAT.VOLE;
            System.out.print("Je prends "+ressources_prises+" ressources de " + choosenType +" au joueur n°"+index);
            try
            {
                increaseRessourceAmout( choosenType,ressources_prises);
            } catch (RemoteException re) { System.out.println(re) ; }
        }
        else
        {
            System.out.println("Je me suis fais avoir :-(");
            etat=ETAT.PENALITE2; // devient indisponible 2 tours
        }
        
    }


    // a une chance sur 2 d'observer
    void comportement_attentionnel()
    {
        Random r = new Random();
        int choix = r.nextInt(2 ); // choisit soit la première ressource, soit la deuxième, s'il reste une deuxième ressource à chercher
        if(choix == 1)
            comportement_individualiste();
        else
            etat = ETAT.OBSERVE;
        
    
    }
    
    // a une chance sur 2 d'attaquer le 1 er ou 2ème joueur avec le plus de ressources
    void comportement_attack()
    {
        Random r = new Random();
        int choix = r.nextInt( 2 ); // choisit soit la première ressource, soit la deuxième, s'il reste une deuxième ressource à chercher
        int succes, attacked ,index = 0, max=-1, turns= 2; 
        int i;
        
        if(choix == 1)
            comportement_individualiste();
        else
        {
            String action ;
            // 1/10 : echec critique, 1/10 : succès critique, 1/10 : rien, 6/10 : 2 tour pénalité, 2/10 : 1 tour pénalité
            succes = r.nextInt( 10 );
            switch(succes)
            {
                case 0:
                {
                    System.out.println("Echec critique");
                    etat = ETAT.PENALITE2;
                    return;
                }
                case 1:
                {
                    System.out.println("Coup critique");
                    action = "coup critique";
                    turns = 3;
                    break;
                }
                default:
                {
                    if( succes == 2|| succes ==3) // rien
                    {
                        System.out.println("J'ai pas réussi mon coup");
                        etat = ETAT.ATTEND;
                        return;
                    }
                    else
                    {
                        System.out.println("Je fais une attaque normale");
                        action = "attaque normale";
                    }
                }
            }
            
            SerializableList <Integer> LJoueurs  = new SerializableList <Integer>() ; // Pour chaque joueur, liste des types et quantitée de ressources
            try
            {
                for (i=0 ; i < C.JList.size() ; i++)
                    LJoueurs.add( C.JList.get(i).totalStockQuantity() );
            }  catch (RemoteException re) { System.out.println(re) ; }
            
            attacked = r.nextInt(Math.min(2, C.JList.size())); // s'il y a au moins 2 joueurs alors attaque l'un des 2 meilleurs
            
            for(i=0;i<C.JList.size();i++)
            {
                if (i == I.IdJoueur)
                    continue;
                if( LJoueurs.get(i) > max )
                {
                    max = LJoueurs.get(i);
                    index = i;
                }
            }
            
            try
            {
                C.JList.get(attacked).attacked(turns); // attaque le joueur
            } catch (RemoteException re) { System.out.println(re) ; }
        
        }
    }
    
    public void attacked( int turns)
    {
        if(turns == 2)
        {
            System.out.println("J'ai été attaqué et je saute " + turns );
            etat = ETAT.PENALITE2;
        }
        else
        {
            System.out.println("J'ai été attaqué et je saute " + turns );
            etat = ETAT.PENALITE3;
        }
    }
    
    // test de fin de jeu
    public void victory_test()
    {
        int i = 0, k = 0;
        //System.out.println("**** TEST victoire *****");
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
                    // Envoie d'abord à tous les joueurs un msg pour qu'ils le suppriment
                    C.deleteToAllPlayer(id);
                    // supprime le joueur de la liste des joueurs actifs du coordinateur en l'ajoutant à la liste des joueurs ayant finis
                    M.deletePlayer(id); 
                    if(C.JList.size() != 0) // besoin car sinon division par 0 et ça fait tout planter
                    {
                        if( C.JList.size() == C.FinishedPlayerList.size() ) // Tous on fini 
                            return ;
                        else
                        { // l'envoie au prochain qui n'a pas encore fini
                            i = (id +1) % C.JList.size();
                            while( C.FinishedPlayerList.contains( C.JList.get(i) ) ) // parcours tous les joueurs et regarde s'ils ont déjà finis
                                i = (i+1) % C.JList.size();
                          //  System.out.println("Envoie token a " + i);
                            if( I.playMode == 0)
                            {
                                have_token = false;
                                
                                if ( I.IdJoueur + 1 == C.JList.size() ) // dernier joueur du tour -> Tous les producteurs augmentent leurs ressources
                                {
                                    for(k=0 ; k < C.PList.size() ; k++)
                                        C.PList.get(k).addStockTurn();
                                }
                                C.JList.get( i ).receiveToken();
                            }
                        }
                    }
                    else
                        System.out.println("La liste est vide");
                }
            }
            else
            {
                if(C.JList.size() != 0) // besoin car sinon division par 0 et ça fait tout planter
                {
                    i = (id +1) % C.JList.size();
                    while( C.FinishedPlayerList.contains( C.JList.get(i) ) ) // parcours tous les joueurs et regarde s'ils ont déjà finis
                        i = (i+1) % C.JList.size();

                    if(I.playMode == 0)
                    {
                        have_token = false;
                        if ( I.IdJoueur + 1 == C.JList.size() ) // dernier joueur du tour -> Tous les producteurs augmentent leurs ressources
                        {
                            for(k=0 ; k < C.PList.size() ; k++)
                                C.PList.get(k).addStockTurn();
                        }
                        C.JList.get( i ).receiveToken();
                        System.out.println("Send token to " + i);
                    }
                }
                else
                    System.out.println("La liste est vide");
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
//                    System.out.println("je fais le test de ressource pour gagner, il me faut " + RWIN.getStock()*(1+id) + " et j'ai " + RJ.getStock());
                    if( RJ.getStock() < RWIN.getStock() ) // pas assez de ressources
                    {
                    //    System.out.println("Pour gagner, il faut "+RWIN.getStock()+" "+RWIN.getStockType()+" et j'ai "+ RJ.getStock());
                        return false;
                    }
                    break; // passe à la prochaine ressource RWIN
                }
            }
        }
        return true;
    }


    public synchronized SerializableList<Tuple<TYPE,Integer>> getStock()
    {
        int i;
        SerializableList<Tuple<TYPE,Integer>> RepL = new SerializableList<Tuple<TYPE,Integer>>(); 
        for(i = 0 ; i < RList.size() ;i++)
        {
            RepL.add( new Tuple<TYPE,Integer> ( RList.get(i).getStockType(), RList.get(i).getStock() ) );
        }
        return RepL;
    }
    
    public synchronized int totalStockQuantity()
    {
        int i;
        int total =0;
        for(i = 0 ; i < RList.size() ;i++)
        {
            total += RList.get(i).getStock() ;
        }
        return total;
    }


     /**
     * Retourne le nombre de ressources du joueur 
     * en prévelant une quantité voulue en paramètre.
     *
     *<p>
     * Fonction utilisée pour le vol sur un joueur
     *</p> 
     *
     * @param t
     *            Le type de la ressource.
     * @param quantity
     *            La quantité de ressource désirée.
     * @return takenRessources
     *            Le nombre de ressources qu'on a pu voler
     */
    public int getStock(int quantity,TYPE t)
    throws RemoteException
    {
        int takenRessources=0;
        int nbressources_joueur=0;

        if (quantity > 15)
            quantity = 15;
        System.out.println("Je me fais dérober des ressources en " + t);
        
        if ( etat == ETAT.OBSERVE )
        {
            System.out.println("J'observe et j'ai vu qu'on essaie de me voler !!!");
            return -1;
        }
        
        synchronized (ObjSynchro) // section critique
        {
            for(int i=0 ; i < RList.size() ; i++)
            {
                if(RList.get(i).getStockType()==t)
                {
                    nbressources_joueur+=RList.get(i).getStock();
                    if(nbressources_joueur<=quantity) // On a demandé tout ce que le joueur possédait ou plus
                    {
                        System.out.println("On m'a vidé mon "+t);
                        RList.get(i).setRessource(0);
                        return nbressources_joueur;
                    }
                    else if(nbressources_joueur>quantity && nbressources_joueur>0) // on a demandé moins que ce que le joueur avait
                    {
                        System.out.println("On m'a volé "+quantity+" de mon "+t);
                        RList.get(i).setRessource(nbressources_joueur-quantity);
                        return quantity;
                    }
                }
            }
            return 0; // si le joueur n'avait rien de la ressource demandée
        }
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
        have_token = true;
        synchronized(monitor)
        {monitor.notify();}
    }
    
    public void VolRessources(int nb, int id, String Res)
    {
        if( etat == ETAT.PENALITE3) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE2;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        if( etat == ETAT.PENALITE2) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else if( etat == ETAT.PENALITE) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.ATTEND;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else
        {
            int ressources_prises =0;
            TYPE choosenType;
            if( Res == "OR")
                choosenType = TYPE.OR;
            else if( Res == "BOIS")
                choosenType = TYPE.BOIS;
            else
                choosenType = TYPE.ARGENT;
            
             // cherche la ressource chez le joueur
            try
            {
                ressources_prises = C.JList.get(id).getStock( nb , choosenType) ;
            }catch (RemoteException re) { System.out.println(re) ; }
                        
            if( ressources_prises >= 0) // le joueur a donné ressources
            {
                etat = ETAT.VOLE;
                System.out.print("Je prends "+ressources_prises+" ressources de " + choosenType +" au joueur n°"+id);
                try
                {
                    increaseRessourceAmout( choosenType,ressources_prises);
                } catch (RemoteException re) { System.out.println(re) ; }
            }
            else
            {
                System.out.println("Je me suis fais avoir :-(");
                etat=ETAT.PENALITE;
            }
            try
            {
                System.out.println(". Tour joueur terminé en état "+etat);
                System.out.println("Mon id " + id);
                M.sendInformation(id, RList);
                TimeUnit.SECONDS.sleep(1);
                
                // test de victoire et passe le jeton
                victory_test();
                if( C.JList.size() == C.FinishedPlayerList.size() ) // Tous on fini 
                {
                    C.endAllProducteurs();
                    return ;
                }
            }
                catch (InterruptedException re) { System.out.println(re) ; }
                catch (RemoteException re) { System.out.println(re) ; }
        }
    }
    
    
    public void PrendRessources(int nb, int id, String Res)
    {
        if( etat == ETAT.PENALITE3) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE2;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        if( etat == ETAT.PENALITE2) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else if( etat == ETAT.PENALITE) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.ATTEND;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else
        {
            int ressources_prises =0;
            TYPE choosenType;
            if( Res == "OR")
                choosenType = TYPE.OR;
            else if( Res == "BOIS")
                choosenType = TYPE.BOIS;
            else
                choosenType = TYPE.ARGENT;
            
            
            // cherche la ressource chez le producteur
            try
            {
                ressources_prises = C.PList.get(id).getStock( nb , choosenType) ;
            }catch (RemoteException re) { System.out.println(re) ; }
                        
            if( ressources_prises > 0) // le producteur a des ressources
            {
                etat = ETAT.PREND_RESSOURCES;
                System.out.print("Je prends "+ressources_prises+" ressources de " + choosenType +" au producteur n°"+id);
                try
                {
                    increaseRessourceAmout( choosenType,ressources_prises);
                } catch (RemoteException re) { System.out.println(re) ; }
            }
            else
            {
                System.out.print("Pas assez de ressource disponibles");
                etat=ETAT.ATTEND;
            }
            
            try
            {
                System.out.println(". Tour joueur terminé en état "+etat);
                System.out.println("Mon id " + id);
                M.sendInformation(id, RList);
                TimeUnit.SECONDS.sleep(1);
                
                // test de victoire et passe le jeton
                victory_test();
                if( C.JList.size() == C.FinishedPlayerList.size() ) // Tous on fini 
                {
                    C.endAllProducteurs();
                    return ;
                }
            }
                catch (InterruptedException re) { System.out.println(re) ; }
                catch (RemoteException re) { System.out.println(re) ; }
        }
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
    
    public int getId()
        throws RemoteException
    {
        return id;
    }
    
    public void deletePlayer(int id)
        throws RemoteException
    {
        C.deletePlayer(id);
    }
    
    public void Observer()
    {

        if( etat == ETAT.PENALITE3) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE2;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        if( etat == ETAT.PENALITE2) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.PENALITE;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else if( etat == ETAT.PENALITE) // c'est fait prendre en volant au tour d'avant
        {
            System.out.println("Je dois attendre ce tour");
            etat = ETAT.ATTEND;
            if( I.playMode == 1 ) 
                try {Thread.sleep( 50 );}
                    catch (InterruptedException re) { System.out.println(re) ; }                
        }
        else
        {
            etat = ETAT.OBSERVE;
            
            try
            {
                System.out.println(". Tour joueur terminé en état "+etat);
                M.sendInformation(id, RList);
                TimeUnit.SECONDS.sleep(1);
                
                // test de victoire et passe le jeton
                victory_test();
                if( C.JList.size() == C.FinishedPlayerList.size() ) // Tous on fini 
                {
                    C.endAllProducteurs();
                    return ;
                }
            }
                catch (InterruptedException re) { System.out.println(re) ; }
                catch (RemoteException re) { System.out.println(re) ; }
        }
    }

    
}

