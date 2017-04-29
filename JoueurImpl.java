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
    public COMPORTEMENT comportement= COMPORTEMENT.COOPERATIF;
    public ETAT etat = ETAT.ATTEND;
    static Object ObjSynchro = new Object();

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
           // System.out.println("Le joueur reçoit l'id : " + I.IdJoueur + ", RI : " + I.nbRessourcesInitiales + ", RD : " + I.nbRessourcesDifferentes);
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
             default: 
            {
                this.comportement = COMPORTEMENT.COOPERATIF;
                System.out.println("Comportement par défaut du joueur : coopératif.");
                break;
            }
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

        int parcours_prod=0;
        int parcours_joueurs=0;
        boolean begin = true;
//        boolean debut = true;
        while(game)
        {
            try
            {
                if (I.playMode == 0 || begin) // mode tour/tour
                {
                    synchronized (monitor)
                    {
                        if ( C.JList.size() != 0 )//&& debut) // besoin car sinon division par 0 et ça fait tout planter
                        {
                            while ( have_token == false)
                            {
                                monitor.wait(100);
                            }
                        }
                    }
                    begin = false;
                }
                // Maintenant on a le jeton => Comportement joueur activé !

                if(this.comportement == COMPORTEMENT.INDIVIDUALISTE)
                {
                    comportement_individualiste();
                }

                if(this.comportement == COMPORTEMENT.COOPERATIF)
                {
                    comportement_cooperatif();  
                }

                if(this.comportement == COMPORTEMENT.VOLEUR)
                {
                    comportement_voleur();
                }
                     /* -Traitre

                    Mode coopératif et puis devient voleur

                    -> observe un producteur 
                        - si il a au moins la moitié des ressources nécessaire alors il se sert (10 max)
                          puis passe en mode voleur
                        - si le producteur en a, mais pas assez alors il attend
                        - si le producteur n'en produit pas alors il passe au producteur suivant
                            - fin du tour
                    ----------------------------------------------------------- */


                //System.out.println("je prend des ressources " + C.PList.get(0).getStock( 10 , TYPE.BOIS));

                // ici il devrait prendre un nombre aléatoire (entre 1 et 10 par ex) de ressource de type aléatoire
               /*

                int ressources_prises = C.PList.get(0).getStock( 9 , TYPE.OR);
                System.out.println("Je prend "+ressources_prises+" ressources d'or !");
                increaseRessourceAmout(TYPE.OR,ressources_prises);
                */

                System.out.println("Tour joueur terminé en état "+etat);
                M.sendInformation(id, RList);
                TimeUnit.SECONDS.sleep(1);
                if(id == 1)
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


    /*****************************************
    * Ici on va implémenter les comportements
    *****************************************
    - Individualiste : 
    * Recherche en priorité la ressource qui existe le moins (et qui sera donc le moins produite)
    * Prend le max qu'il a besoin pour cette ressource
    * 
    Prend autant de ressources que possible pour atteindre l'objectif (9 par tour).
    -> Demande au premier producteur si il a les ressources nécessaires...
    - Si oui, alors il se sert comme un rapiat et en prend 9.
    - Si non, alors il demande au producteur suivant.
    - puis il termine son tour.
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
        for (i=0 ; i< TypeNb.size() ; i++)
        {
            tmp = TypeNb.get(i).y * 10 + TypeQuantite.get(i).y;
            if( min == -1 && TypeQuantite.get(i).y != 0) // initialisation +évite de chercher des ressources quand il n'y en a aucune de disponible
            {
                min = tmp;
                index = i;
            }
            else if (tmp < min && TypeQuantite.get(i).y !=0) // évite de chercher des ressources quand il n'y en a aucune de disponible
            {
                min = tmp;
                index = i;
            }
        }
        
        
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
            System.out.println("Je prends "+ressources_prises+" ressources de " + TypeNb.get(index).x +" or au producteur n°"+index2);
            try
            {
                increaseRessourceAmout( TypeNb.get(index).x,ressources_prises);
            } catch (RemoteException re) { System.out.println(re) ; }
        }
        else
        {
            System.out.println("Pas assez de ressource nécessaire dans le producteur n°"+index2);
            System.out.println("(nombre de producteurs dispo : "+C.PList.size()+") On passe au suivant !");
            etat=ETAT.ATTEND;
        }
        
    }
    


    /*
    -Coopératif
    Observe les joueurs et prends des ressources si le producteur en as produit au moins
    la moitié du nombre de ressources à atteindre

    -> observe un producteur 
        - si il a au moins la moitié des ressources nécessaire alors il se sert (10 max)
        - si le producteur en a , mais pas assez alors il attend
        - si le producteur n'en produit pas alors il passe au producteur suivant
        - fin du tour
    -----------------------------------------------------
    */
    void comportement_cooperatif()
    {
        int i;
        Ressource RWIN, RJ;
        SerializableList<Ressource> LWIN = I.VLC ;
        int parcours_prod=0, ressources_prises =0;
        for( i = 0 ; i < LWIN.size() ; i++ )
        {
            RWIN = I.VLC.get(i);
            RJ = RList.get(i);

            if( RJ.getStockType() == RWIN.getStockType() )
            {
                //System.out.println("je fais le test de ressource pour gagner, il me faut " + RWIN.getStock() + " et j'ai " + RJ.getStock());
                if( RJ.getStock() < RWIN.getStock() && RWIN.getStock()>0) // si le joueur a encore besoin de ressources pour gagner
                {
                    try
                    {
                        ressources_prises = C.PList.get(parcours_prod).getStock(9, RWIN.getStockType());
                    }
                    catch (RemoteException re) { System.out.println(re) ; }
                    // Test
                    if(ressources_prises >0) // le producteur a des ressources
                    {
                        if (ressources_prises >= RWIN.getStock()/2)
                        {
                            etat=ETAT.PREND_RESSOURCES;
                            System.out.println("Je prends "+ressources_prises+" ressources d'or au producteur n°"+parcours_prod);
                            try
                            {
                                increaseRessourceAmout(RWIN.getStockType(),ressources_prises);
                            }
                            catch (RemoteException re) { System.out.println(re) ; }
                        }
                        else
                        {
                            etat=ETAT.ATTEND;
                            System.out.println("Prod n'a pas encore atteint /2 des R. nécessaires => Attente et tour suivant");
                            //System.out.print("Les producteurs n'ont pas encore produit plus la moitié ");
                            //System.out.println("du nombre de ressource nécessaires à la victoire, alors j'attends et je passe au suivant !");
                            //System.out.println("(nombre de producteurs dispo : "+C.PList.size()+") !");
                            parcours_prod = (parcours_prod +1) %C.PList.size();
                        }
                    }
                    else
                    {
                        etat=ETAT.ATTEND;
                        System.out.println("Plus de ressource nécessaire dans le producteur n°"+parcours_prod);
                        System.out.println("(nombre de producteurs dispo : "+C.PList.size()+") On passe au suivant !");
                        parcours_prod = (parcours_prod +1) %C.PList.size();
                    }
                }
            }
        }     
    }



    /* -Voleur :

    Observe les joueurs et leur vole des ressources.
    -> Demande au premier joueur si il a des ressources nécessaire pour gagner
    - Si oui alors il les prends (15 max par tour) 
    - Si non alors il observe le joueur suivant
    - si après avoir parcouru tous les joueurs il n'y en a aucun qu'on peut voler
        alors le voleur devient un individualiste pour 1 tour puis redevient voleur.
    - puis il termine son tour et recommence
    ----------------------------------------------------- */
    void comportement_voleur()
    {
        int i;
        Ressource RWIN, RJ;
        SerializableList<Ressource> LWIN = I.VLC ;
        int parcours_prod=0, parcours_joueurs = 0,ressources_prises =0;
        for( i = 0 ; i < LWIN.size() ; i++ )
        {
            RWIN = I.VLC.get(i);
            RJ = RList.get(i);

            if( RJ.getStockType() == RWIN.getStockType() )
            {
                //System.out.println("je fais le test de ressource pour gagner, il me faut " + RWIN.getStock() + " et j'ai " + RJ.getStock());
                if( RJ.getStock() < RWIN.getStock() && RWIN.getStock()>0) // si le joueur a encore besoin de ressources pour gagner
                {
                    if(etat==ETAT.ATTEND)
                    {
                        System.out.println("Observe le joueur "+parcours_joueurs);
                        etat=ETAT.OBSERVE;
                    }
                    else if (etat==ETAT.OBSERVE || etat==ETAT.VOLE) 
                    {
                        try
                        {
                            ressources_prises = C.PList.get(parcours_prod).getStock(9, RWIN.getStockType());
                        }
                        catch (RemoteException re) { System.out.println(re) ; }
                        if(ressources_prises >0) // le joueur a des ressources
                        {
                            etat=ETAT.VOLE;
                            System.out.println("Je vole "+ressources_prises+" ressources de "+ RWIN.getStockType()+" au joueur n°"+parcours_joueurs);
                            try
                            {
                                increaseRessourceAmout(RWIN.getStockType(),ressources_prises);
                            }
                            catch (RemoteException re) { System.out.println(re) ; }
                        }
                        else
                        {
                            System.out.println("Pas assez de ressource nécessaire dans le joueur n°"+parcours_joueurs);
                            System.out.println("(nombre de joueurs dispo : "+C.JList.size()+") On passe au suivant (joueur "+((parcours_joueurs +1) %(C.JList.size()-1))+")");
                            parcours_joueurs = (parcours_joueurs +1) %C.JList.size()-1;
                            etat=ETAT.ATTEND;
                        }
                    }
                    parcours_joueurs = (parcours_joueurs+1) %C.JList.size()-1;
                }
            }
        }
    }
    
    // test de fin de jeu
    public void victory_test()
    {
        int i = 0;
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
                  //  System.out.println("Envoie token a " + i);
                    if(I.playMode == 0)
                    {
                        have_token = false;
                        C.JList.get( i ).receiveToken();
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
                    if( RJ.getStock() < RWIN.getStock()* (1+id) ) // pas assez de ressources
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

        System.out.println("Je me fais dérober des ressources en " + t);
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
}

