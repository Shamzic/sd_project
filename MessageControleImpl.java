import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.io.PrintWriter ;
import java.io.IOException ;
import java.util.Date ;
import java.util.concurrent.TimeUnit;


public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
    public InitialInfoImpl I;
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial

    public SerializableList<Tuple> SList = new SerializableList<Tuple>(); // Liste avec les coordonnées des joueurs ( NomDeMachine , port)
    public SerializableList<Tuple> prodCoordList = new SerializableList<Tuple>(); // liste avec les coordonnées des producteurs ( NomDeMachine , port)
    public SerializableList<SerializableList<TYPE>> ListProducteurRTypes = new SerializableList<SerializableList<TYPE>>();
    public ArrayList<Connexion> CList = new ArrayList<Connexion>(); // Liste contenant les objets connexion avec lesquels on communique des infos du contrôleur aux joueurs ( ex un nouveau joueur / producteur s'est ajouté
    public ArrayList<Producteur> PList = new ArrayList<Producteur>(); // Liste de objets producteur avec lesquels on communique avec les producteurs
    public ArrayList<Joueur> JList = new ArrayList<Joueur>(); // Liste d'objet joueurs avec lesquels on communique avec les joueurs
    public ArrayList<Joueur> FinishedPlayerList = new ArrayList<Joueur>();
    Date D ;
    long beginTime;
    
    PrintWriter writer; // objet avec lequel on écrit dans le fichier
    int turn = 1; // tour ( sert à écrire dans le fichier )
    Thread T;
    
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes, int nbProducteurs, int nbJoueurs, String Name, int port, int victory_condition, SerializableList<Ressource> L, int playMode, int playTime)
    throws RemoteException
    {
        int i;
        I = new InitialInfoImpl( nbRessourcesInitiales, nbRessourcesDifferentes, Name, port, nbProducteurs, nbJoueurs, victory_condition, L, playMode);
        try
        {
            writer = new PrintWriter("actionLog.dat","UTF-8");
        }
        catch (IOException e) { System.out.println(e) ; }
        
        if(victory_condition == 2 || playMode == 1 ) // arrêt au temps
        {
            if (victory_condition == 2)
            {
                for(i= 0 ; i < L.size() ; i++) // met les ressources à "infini" 
                {
                    L.get(i).setRessource(10000000);
                }
            }
            T = new Thread()
            {
                public void run()
                {
                    int i;
                    System.out.println("Compte à rebours lancé");
                    try { TimeUnit.SECONDS.sleep(playTime); } catch (InterruptedException re) { System.out.println(re) ; }
                    System.out.println("Temps écoulé. Fin de partie.");
                    try
                    {
                    for(i=0 ; i < JList.size() ; i++)
                        JList.get(i).end();
                    for(i=0 ; i < PList.size() ; i++)
                        PList.get(i).end();
                    }catch (RemoteException re) { System.out.println(re) ; }
                }
            };
        }
        System.out.println("J'attends que " + nbJoueurs + " se connectent");
    }
    
    // Envoie les infos initiales au joueur
    public InitialInfoImpl getPlayerInitialInfo()
    throws RemoteException
    {
        I.IdJoueur++;
        return I;
    }
    
    // Envoie les infos initiales au producteur
    public InitialInfoImpl getProducteurInitialInfo()
    throws RemoteException
    {
        I.IdProducteur ++;
        return I;
    }
    
    // Add le joueur de la machine MachineName et du port port
    public void addMachine( String MachineName, int port)
    throws RemoteException
    {
        int i;
        // Créé d'abord la nouvelle connexion
        try
        {
            Connexion CNew = (Connexion) Naming.lookup("rmi://" + MachineName + ":" + port + "/Connexion");

            CList.add(CNew); // l'ajoute à la list de connexions
            SList.add(new Tuple<String,Integer>(MachineName,port)); // l'ajoute à la liste de tuples (MachineName,port) servant à l'interconnexion des différents hôtes
            
            
            CNew.initialSetPlayer(SList); // envoie au joueur qui s'est connecté les coordonnées des autres joueurs
            CNew.setProducteur(prodCoordList); // envoie au joueur le nécessaire pour qu'il puisse se connecter aux producteurs
            // Maintenant envoie les coordonnées du nouveau connecté à tous les agents
            for( i = 0 ; i < CList.size() -1 ; i ++) // l'envoie à tout le monde sauf au joueur qui vient de se connecter
                CList.get(i).addConnexionPlayer(MachineName,port);

            
            Joueur J = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur"); // établi une connexion avec le joueur
            JList.add(J);            
            System.out.println("J'ai ajouté le joueur " + MachineName + " port : " + port);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
        if( (JList.size()   == I.nbJoueurs  ) &&( I.IdProducteur == I.nbProducteurs -1 ))
        {
            System.out.println("Le jeu va commencer avec " + JList.size() + " Joueurs");
            
            // lance tous les producteurs
            if( I.playMode != 0 ) // pas le mode tour / tour
            {
                for(i=0;i<PList.size() ; i++)
                    PList.get(i).fonctionThread(2000);
            }
            
            
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
            
            if( I.playMode == 0) // mode tour/tour
                JList.get(0).receiveToken(); // lance le 1er joueur
            else // autre mode
            {
                D = new Date();
                beginTime = D.getTime();
                for(i =0 ; i< JList.size() ;i++)
                    JList.get(i).receiveToken();
            }
            if( I.victory_condition == 2 || I.playMode == 1 )
                T.start();
        }
        else
            System.out.println("Il manque " + (I.nbJoueurs - I.IdJoueur -1) + " Joueurs et " + (I.nbProducteurs - I.IdProducteur -1) + " Producteurs ");
    }
    
    // Add le producteur de la machine MachineName et du port port
    public void addProducteur( String MachineName, int port)
    throws RemoteException
    {
        int i;
        try
        {
            prodCoordList.add(new Tuple<String,Integer>(MachineName,port)); // l'ajoute à la liste contenant les coordonnées du joueur
            Producteur P = (Producteur) Naming .lookup("rmi://" + MachineName + ":" + port + "/Producteur"); // établi une connexion avec le producteur
            PList.add(P); // Ajoute le producteur à la liste des producteurs
            for( i = 0 ; i < CList.size() ; i ++)
                CList.get(i).addConnexionProducteur(MachineName,port);

            System.out.println("J'ai ajouté le producteur " + MachineName + " port : " + port);
            SerializableList<TYPE> LISTE = P.getStockTypes();
            ListProducteurRTypes.add(LISTE); // ajoute la liste des types de ressources produites par ce producteur
            for ( i = 0 ; i< LISTE.size() ; i++)
                System.out.println( "Ressource "  +i +" : "+ LISTE.get(i) );
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        

        if( (JList.size()   == I.nbJoueurs ) &&( I.IdProducteur == I.nbProducteurs -1 ))
        {
            System.out.println("Le jeu va commencer");
            
            // lance tous les producteurs
            if( I.playMode != 0 ) // pas le mode tour / tour
            {
                for(i=0;i<PList.size() ; i++)
                    PList.get(i).fonctionThread(2000);
            }            
            try { TimeUnit.SECONDS.sleep(1); } catch (InterruptedException re) { System.out.println(re) ; }
            
            if( I.playMode == 0) // mode tour/tour
                JList.get(0).receiveToken(); // lance le 1er joueur
            else // autre mode
            {
                D = new Date();
                beginTime = D.getTime();
                for(i =0 ; i< JList.size() ;i++)
                    JList.get(i).receiveToken();
            }
            if(I.victory_condition == 2 || I.playMode == 1  )
                T.start();
        }
        else
            System.out.println("Il manque " + (I.nbJoueurs - I.IdJoueur -1) + " Joueurs et " + (I.nbProducteurs - I.IdProducteur -1) + " Producteurs ");
        
    }
    
    /* Joueur envoie les informations du tour au coordinateur qui l'écrit dans un fichier
     * Au tour/tour =>TURN_NB  IdJoueur <Liste des ressources du oueur>
     * Autre mode => TIME IdJoueur <Liste des ressources du joueur
    */
    public void sendInformation(int idPlayer, SerializableList<Ressource> Ressources)
        throws RemoteException
    {
        int i, initId = 0;
        String S;
        if (idPlayer == initId && I.playMode == 0) // tout le monde a joué on revient au joueur init ET on est dans le mode tour/tour
        {
            //writer.print(turn);
            System.out.println("Passe au prochain tour");
            System.out.print("Pour gagner il faut " );
            for( i = 0; i < I.VLC.size() ; i++)
                System.out.print(" " + I.VLC.get(i).stock + " " + I.VLC.get(i).T);
            System.out.println("");
            turn ++;
        }
        if( I.playMode == 0)
            S=""+turn+" "+idPlayer;
        else // sinon écrit time à la place du tour
        {
            Date presentDate = new Date();
            long currentTime = presentDate.getTime() - beginTime;
            S = "" + currentTime + " " + idPlayer;
        }
        // On cherche le joueur avec l'id minimal qui est encore actif
        while ( FinishedPlayerList.contains(JList.get(initId)) )
        {
            initId ++;
        }
            
        if(FinishedPlayerList.size() == JList.size()) // tout le monde est fini
        {
            System.out.println("Le jeu à fini");
            writer.close();
            return;
        }

        
        for(i = 0 ; i < TYPE.values().length ; i++ )
        {
            S +=" " + Ressources.get(i).getStock();
        }
        System.out.println(S);
        
        writer.println(S);
        
    }
    
    
    // Envoie la liste des types de tous les producteurs au joueur qui la demande
    // Dans chaque entrée i de la liste se trouve une liste contenant les types de ressources du producteur i
    public SerializableList<SerializableList<TYPE>> getStocksTypesAllProducteurs()
    {
        return ListProducteurRTypes;
    }
    
    
    
    public int getIdProducteur()
        throws RemoteException
    {
        System.out.println("Give id " + I.IdProducteur );
        return I.IdProducteur ++;
    }
    
    public void GameEnded()
        throws RemoteException
    {
        writer.close();
    }
    
    public void deletePlayer( int id)
        throws RemoteException
    {
        int i = 0;
        // ajoute le joueur de façon à ce que la liste soit triée
        while( i < FinishedPlayerList.size() - 1 && id < FinishedPlayerList.get(i).getId() ) 
            i++;
        FinishedPlayerList.add( i, JList.get(id) );
    }
    
    
}

