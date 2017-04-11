import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 
import java.io.PrintWriter ;
import java.io.IOException ;

public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
    public static final long serialVersionUID = 1L; // Utilie uniquement pour régler les warning de serial
	public int IdProducteur = 0, IdJoueur = 0; // id des producteurs et des joueurs
    public int nbProducteurs, nbJoueurs; // nombre de joueurs et producteurs qu'on veut 
    
    public int nbRessourcesInitiales, nbRessourcesDifferentes; 
    public String Name; // nom de la machine du contrôleur
    public int port; // port du rmiregistry du contrôleur
    public SerializableList<Tuple> SList = new SerializableList<Tuple>(); // Liste avec les coordonnées des joueurs ( NomDeMachine , port)
    public SerializableList<Tuple> prodCoordList = new SerializableList<Tuple>(); // liste avec les coordonnées des producteurs ( NomDeMachine , port)
    public SerializableList<SerializableList<TYPE>> ListProducteurRTypes = new SerializableList<SerializableList<TYPE>>();
    public ArrayList<Connexion> CList = new ArrayList<Connexion>(); // Liste contenant les objets connexion avec lesquels on communique des infos du contrôleur aux joueurs ( ex un nouveau joueur / producteur s'est ajouté
    public ArrayList<Producteur> PList = new ArrayList<Producteur>(); // Liste de objets producteur avec lesquels on communique avec les producteurs
    public ArrayList<Joueur> JList = new ArrayList<Joueur>(); // Liste d'objet joueurs avec lesquels on communique avec les joueurs
    
    PrintWriter writer; // objet avec lequel on écrit dans le fichier
    int turn = 1; // tour ( sert à écrire dans le fichier )

    
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes, int nbProducteurs, int nbJoueurs, String Name, int port)
    throws RemoteException
    {
        this.nbRessourcesInitiales = nbRessourcesInitiales;
        this.nbRessourcesDifferentes = nbRessourcesDifferentes;
        this.Name = Name;
        this.port = port;
        this.nbProducteurs = nbProducteurs;
        this.nbJoueurs = nbJoueurs;
        try
        {
             writer = new PrintWriter("actionLog.txt","UTF-8");
        }
        catch (IOException e) { System.out.println(e) ; }
    }
    
    // Envoie les infos initiales au joueur
    public TripleImpl getPlayerInitialInfo()
    throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdJoueur++, nbRessourcesInitiales, nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales pour joueur");
        return T;
    }
    
    
    // Envoie les infos initiales au producteur
    public TripleImpl getProducteurInitialInfo()
        throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales,nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales pour producteur");
        return T;
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
        
        if( (IdJoueur  == nbJoueurs) &&  ( IdProducteur  == nbProducteurs))
        {
            System.out.println("Le jeu va commencer");
            JList.get(0).receiveToken();
        }
        else
            System.out.println("Il manque " + (nbJoueurs - IdJoueur) + " Joueurs et " + (nbProducteurs - IdProducteur) + " Producteurs ");
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
            P.fonctionThread(3000, 5);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        

        if( (IdJoueur  == nbJoueurs) &&( IdProducteur == nbProducteurs))
            JList.get(0).receiveToken();
        else
            System.out.println("Il manque " + (nbJoueurs - IdJoueur) + " Joueurs et " + (nbProducteurs - IdProducteur) + " Producteurs ");
        
    }
    
    /* Joueur envoie les informations du tour au coordinateur qui l'écrit dans un fichier
     * =>TOUR NB
     * IdJoueur <Liste des ressources par joueur  
    */
    public void sendInformation(int idPlayer, SerializableList<Ressource> Ressources)
        throws RemoteException
    {
        int i;
        String S=""+idPlayer;

        for(i = 0 ; i < TYPE.values().length ; i++ )
        {
            S += " " + Ressources.get(i).getStock();
            System.out.println(S);
            writer.println(S);
            //System.out.println( " val du type " + Ressources.get(i).getStockType() + " a une valeur de "  + Ressources.get(i).getStock());
        }
        if(idPlayer == IdJoueur -1)
        {
            System.out.println("Passe au prochain tour");
            turn ++;
        }
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
        System.out.println("Give id " + IdProducteur );
        return IdProducteur ++;
    }
    
    
}

