import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.io.PrintWriter ;
import java.io.IOException ;

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
    
    PrintWriter writer; // objet avec lequel on écrit dans le fichier
    int turn = 1; // tour ( sert à écrire dans le fichier )

    
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes, int nbProducteurs, int nbJoueurs, String Name, int port, int victory_condition, SerializableList<Ressource> L)
    throws RemoteException
    {
        I = new InitialInfoImpl( nbRessourcesInitiales, nbRessourcesDifferentes, Name, port, nbProducteurs, nbJoueurs, victory_condition, L);
        try
        {
            writer = new PrintWriter("actionLog.dat","UTF-8");
        }
        catch (IOException e) { System.out.println(e) ; }
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
            ///////////////// j'ai changé ça de place après le for pour que les joueurs senregistres aussi :::::::::::::::::::::::::::::::::::::
            CList.add(CNew); // l'ajoute à la list de connexions
            SList.add(new Tuple(MachineName,port)); // l'ajoute à la liste de tuples (MachineName,port) servant à l'interconnexion des différents hôtes
            
            
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
        
        if( (I.IdJoueur  == I.nbJoueurs -1 ) &&( I.IdProducteur == I.nbProducteurs -1 ))
        {
            System.out.println("Le jeu va commencer");
            JList.get(0).receiveToken();
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
            prodCoordList.add(new Tuple(MachineName,port)); // l'ajoute à la liste contenant les coordonnées du joueur
            Producteur P = (Producteur) Naming .lookup("rmi://" + MachineName + ":" + port + "/Producteur"); // établi une connexion avec le producteur
            PList.add(P); // Ajoute le producteur à la liste des producteurs
            for( i = 0 ; i < CList.size() ; i ++)
                CList.get(i).addConnexionProducteur(MachineName,port);

            System.out.println("J'ai ajouté le producteur " + MachineName + " port : " + port);
            SerializableList<TYPE> LISTE = P.getStockTypes();
            ListProducteurRTypes.add(LISTE); // ajoute la liste des types de ressources produites par ce producteur
            for ( i = 0 ; i< LISTE.size() ; i++)
                System.out.println( "Ressource "  +i +" : "+ LISTE.get(i) );
            P.fonctionThread(3000); // n/2+1
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        

        if( (I.IdJoueur  == I.nbJoueurs -1 ) &&( I.IdProducteur == I.nbProducteurs -1 ))
            JList.get(0).receiveToken();
        else
            System.out.println("Il manque " + (I.nbJoueurs - I.IdJoueur -1) + " Joueurs et " + (I.nbProducteurs - I.IdProducteur -1) + " Producteurs ");
        System.out.println("" + I.nbJoueurs + "   " +  (-1*I.IdJoueur -1) + " Joueurs et " + I.nbProducteurs  + "   " + (-1*I.IdProducteur -1));
        
    }
    
    /* Joueur envoie les informations du tour au coordinateur qui l'écrit dans un fichier
     * =>TOUR NB
     * IdJoueur <Liste des ressources par joueur  
    */
    public void sendInformation(int idPlayer, SerializableList<Ressource> Ressources)
        throws RemoteException
    {
        int i, initId = 0;
	if (idPlayer == initId) // tout le monde a joué on revient au joueur init
        {
            //writer.print(turn);
            System.out.println("Passe au prochain tour");
            turn ++;
        }  
        String S=""+turn+" "+idPlayer;
        // On cherche le joueur avec l'id minimal qui est encore actif
        while ( FinishedPlayerList.contains(JList.get(initId)) )
        {
            initId ++;
        }
            
        if(FinishedPlayerList.size() == JList.size()) // tout le monde est fini
        {
            System.out.println("Le jeu est fini");
            writer.close();
            return;
        }

        
        for(i = 0 ; i < TYPE.values().length ; i++ )
        {
	    //writer.print(turn);
            S +=" " + Ressources.get(i).getStock();
            //System.out.println( " val du type " + Ressources.get(i).getStockType() + " a une valeur de "  + Ressources.get(i).getStock());
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

