import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 



public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
	public int IdProducteur = 0, IdJoueur = 0; // id des producteurs et des joueurs
    public int nbRessourcesInitiales, nbRessourcesDifferentes; 
    public String Name; // nom de la machine du contrôleur
    public int port; // port du rmiregistry du contrôleur
    public SerializableList<Tuple> SList = new SerializableList<Tuple>(); // Liste avec les coordonnées des joueurs ( NomDeMachine , port)
    public SerializableList<Tuple> prodCoordList = new SerializableList<Tuple>(); // liste avec les coordonnées des producteurs ( NomDeMachine , port)
    public SerializableList<SerializableList<TYPE>> ListProducteurRTypes = new SerializableList<SerializableList<TYPE>>();
    public ArrayList<Connexion> CList = new ArrayList<Connexion>(); // Liste contenant les objets connexion avec lesquels on communique des infos du contrôleur aux joueurs ( ex un nouveau joueur / producteur s'est ajouté
    public ArrayList<Producteur> PList = new ArrayList<Producteur>(); // Liste de objets producteur avec lesquels on communique avec les producteurs
    public ArrayList<Joueur> JList = new ArrayList<Joueur>(); // Liste d'objet joueurs avec lesquels on communique avec les joueurs
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes, int nbProducteurs, String Name, int port)
    throws RemoteException
    {
        int i;
        this.nbRessourcesInitiales = nbRessourcesInitiales;
        this.nbRessourcesDifferentes = nbRessourcesDifferentes;
        this.Name = Name;
        this.port = port;
        
        //~ 
        //~ try
        //~ {            
            //~ // Créé maintenant tous les producteurs
            //~ System.out.println("il y a "+ nbRessourcesInitiales + " ressources au début ");
            //~ for (i=0 ; i < nbProducteurs ; i++)
            //~ {
                //~ ProducteurImpl P = new ProducteurImpl(i,nbRessourcesInitiales,nbRessourcesDifferentes);
                //~ PList.add(P);
                //~ String s ="rmi://localhost:" + 5000 + "/Producteur" + i;
                //~ Naming.rebind( s , P); // Pour se connecter au producteur i, on contact le producteur i 
                //~ System.out.println("Créé le producteur " + i);
            //~ }
         //~ }
        //~ catch (MalformedURLException e) { System.out.println(e) ; }
        
    }
    
    // Envoie les infos initiales au joueur
    public TripleImpl getPlayerInitialInfo()
    throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales, nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales pour joueur");
        return new TripleImpl(IdProducteur, nbRessourcesInitiales, nbRessourcesDifferentes);
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
            CNew.initialSetPlayer(SList); // envoie au joueur qui s'est connecté les coordonnées des autres joueurs
            CNew.setProducteur(prodCoordList); // envoie au joueur le nécessaire pour qu'il puisse se connecter aux producteurs
            
            // Maintenant envoie les coordonnées du nouveau connecté à tous les agents
            for( i = 0 ; i < CList.size() ; i ++)
                CList.get(i).addConnexionPlayer(MachineName,port);
            
            CList.add(CNew); // l'ajoute à la list de connexions
            SList.add(new Tuple(MachineName,port)); // l'ajoute à la liste de tuples (MachineName,port) servant à l'interconnexion des différents hôtes
            
            Joueur J = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur"); // établi une connexion avec le joueur
            JList.add(J);
            
            System.out.println("J'ai ajouté le joueur " + MachineName + " port : " + port);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
        
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
            
            System.out.println("J'ai ajouté le producteur " + MachineName + " port : " + port);
            SerializableList<TYPE> LISTE = P.getRessourceTypes();
            ListProducteurRTypes.add(LISTE); // ajoute la liste des types de ressources produites par ce producteur
            for ( i = 0 ; i< LISTE.size() ; i++)
                System.out.println( "Ressource "  +i +" : "+ LISTE.get(i) );
            P.fonctionThread(1000, 5);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }
    
    // Envoie la liste des types de tous les producteurs au joueur qui la demande
    // Dans chaque entrée i de la liste se trouve une liste contenant les types de ressources du producteur i
    public SerializableList<SerializableList<TYPE>> getRessourceTypesAllProducteurs()
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

