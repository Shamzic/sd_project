import java.util.ArrayList;
import java.rmi.server.UnicastRemoteObject ;
import java.rmi.RemoteException ;
import java.rmi.* ; 
import java.net.MalformedURLException ; 

// objet qui va contenir toutes les connexions (objets avec les autres agents)
// Une méthode de cette classe est appelée à chaque fois que le controlleur ajoute une machine
class ConnexionImpl extends UnicastRemoteObject implements Connexion
{
    
    public ArrayList<Joueur> JList = new ArrayList<Joueur>();
    
    public ArrayList<Producteur> PList = new ArrayList<Producteur>();

    
    public ConnexionImpl()
    throws RemoteException
    {
    }
    
    // Renvoie la quantité de la ressource ressourceNb du producteur producteurNb
    public int getRessourceAmount( int producteurNb, int ressourceNb)
    throws RemoteException
    {
        return PList.get(producteurNb).askRessourceAmount(ressourceNb);
    }
    
    // Établi la connexion avec les autres joueurs contenus dans la lsite L envoyée par le coordinateur
    public void initialSetPlayer( SerializableList<Tuple> L)
    throws RemoteException
    {
        int i;
        try
        {
            for( i = 0 ; i< L.size() ; i++)
            {
                System.out.println("Machine :" + L.get(i).MN + " :" + L.get(i).port);
                Joueur P = (Joueur) Naming.lookup("rmi://" +  L.get(i).MN + ":" + L.get(i).port + "/Joueur") ;
                JList.add( P );
                P.salut();
            }
            
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    
    // Établi la connexion avec les autres producteurs contenus dans la lsite L envoyée par le coordinateurs
    public void setProducteur ( SerializableList<Tuple> PCoordList)
    throws RemoteException
    {
        int i;
        int size = PCoordList.size();
        try
        {
            for(i=0; i < size ; i++)
            {
                System.out.println("Ajoute le producteur " + i + " au port " + PCoordList.get(i).port + " nom de machine : " + PCoordList.get(i).MN);
                Producteur P = (Producteur) Naming.lookup("rmi://" +  PCoordList.get(i).MN + ":"+PCoordList.get(i).port+"/Producteur");
                PList.add(P);
            }
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
    }
    
    // Lorsqu'un joueur est ajouté on appel cette méthode pour le dire à tous les joueurs
    public void addConnexionPlayer(String MachineName, int port)
    throws RemoteException
    {
        System.out.println("J'ai ajouté la machine " + MachineName + ":"+ port);
        try
        {
            Joueur J = (Joueur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Joueur") ;
            JList.add( J );
            J.salut();
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }

    // Lorsqu'un joueur est ajouté on appel cette méthode pour le dire à tous les joueurs
    public void addConnexionProducteur(String MachineName, int port)
    throws RemoteException
    {
        System.out.println("J'ai ajouté la machine " + MachineName + ":"+ port);
        try
        {
            Producteur P = (Producteur) Naming.lookup("rmi://" + MachineName + ":" + port + "/Producteur") ;
            PList.add( P );
            P.salut();
        }
        catch (NotBoundException re) { System.out.println(re) ; }
        catch (MalformedURLException e) { System.out.println(e) ; }
        
    }

    public int takeRessourceAmount( int producteurNb, int ressourceNb)
    throws RemoteException
    {
        return PList.get(producteurNb).askRessourceAmount(ressourceNb);
    }
	
}
