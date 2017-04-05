import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
	public int IdProducteur = 0, IdJoueur = 0;
    public int nbRessourcesInitiales, nbRessourcesDifferentes;
    public String Name;
    public int port;
    public SerializableList SList = new SerializableList();
    public SerializableList prodCoordList = new SerializableList();
    public ArrayList<Connexion> CList = new ArrayList<Connexion>();
    public ArrayList<Producteur> PList = new ArrayList<Producteur>();
    public ArrayList<Joueur> JList = new ArrayList<Joueur>();
    
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
    
    public TripleImpl getPlayerInitialInfo()
    throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales, nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales pour joueur");
        return new TripleImpl(IdProducteur, nbRessourcesInitiales, nbRessourcesDifferentes);
    }
    
    
    
    public TripleImpl getProducteurInitialInfo()
        throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales,nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales pour producteur");
        return T;
    }
    
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
            SList.add(MachineName,port); // l'ajoute à la liste de tuples (MachineName,port) servant à l'interconnexion des différents hôtes
            
            Joueur J = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur"); // établi une connexion avec le joueur
            JList.add(J);
            
            System.out.println("J'ai ajouté le joueur " + MachineName + " port : " + port);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
        
    }
    
    
    public void addProducteur( String MachineName, int port)
    throws RemoteException
    {
        try
        {
            prodCoordList.add(MachineName,port);
            Producteur P = (Producteur) Naming .lookup("rmi://" + MachineName + ":" + port + "/Producteur"); // établi une connexion avec le producteur
            PList.add(P);
            
            System.out.println("J'ai ajouté le producteur " + MachineName + " port : " + port);
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }
    
    
    
    
    
    public int getIdProducteur()
    throws RemoteException
    {
        System.out.println("Give id " + IdProducteur );
        return IdProducteur ++;
    }
    
    
}

