import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.util.ArrayList;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

public class MessageControleImpl extends UnicastRemoteObject implements MessageControle
{
	public int IdProducteur = 0, IdJoueur = 0;
    public int nbRessourcesInitiales, nbRessourcesDifferentes;
    public SerializableList SList = new SerializableList();
    public ArrayList<Connexion> CList = new ArrayList<Connexion>();
    
    public MessageControleImpl(int nbRessourcesInitiales, int nbRessourcesDifferentes)
    throws RemoteException
    {
        this.nbRessourcesInitiales = nbRessourcesInitiales;
        this.nbRessourcesDifferentes = nbRessourcesDifferentes;
    }
    
    public TripleImpl getPlayerInitialInfo()
    throws RemoteException
    {
        TripleImpl T = new TripleImpl(IdProducteur++, nbRessourcesInitiales, nbRessourcesDifferentes);
        System.out.println("je donne les infos initiales");
        return new TripleImpl(IdProducteur, nbRessourcesInitiales, nbRessourcesDifferentes);
    }
    
    public void addMachine( String MachineName, int port)
    throws RemoteException
    {
        int i;
        // Créé d'abord la nouvelle connexion
        try
        {
            Connexion CNew = (Connexion) Naming.lookup("rmi://" + MachineName + ":" + port + "/Connexion");
            CNew.initialSetPlayer(SList); // envoie à l'agent qui s'est connecté les coordonnées des autres agents
            
            // Maintenant envoie les coordonnées du nouveau connecté à tous les agents
            for( i = 0 ; i < CList.size() ; i ++)
                CList.get(i).addConnexion(MachineName,port);
            
            CList.add(CNew); // l'ajoute à la list de connexions
            SList.add(MachineName,port); // l'ajoute à la liste de tuples (MachineName,port) servant à l'interconnexion des différents hôtes
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

